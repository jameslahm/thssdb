package cn.edu.thssdb.parser;

import cn.edu.thssdb.exception.PrimaryKeyEmptyException;
import cn.edu.thssdb.schema.*;
import cn.edu.thssdb.statement.BaseStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EvaluatorTest {
    private Manager manager;
    private SQLEvaluator evaluator;
    private Session session;
    private String currentDatabaseName = "test";
    private String currentTableName = "hello";

    @BeforeAll
    public void setUp(){
        manager = Manager.getInstance();
        session = new Session(1,currentDatabaseName);
        evaluator = new SQLEvaluator(manager,session);
        manager.createDatabaseIfNotExists(currentDatabaseName);
    }

    @Test
    @Order(1)
    public void testCreateTable(){
        String sql = "create table hello (id int not null, " +
                "age long, charge float, salary double " +
                ", name string(256),primary key(id))";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        sql = "create table hi (id int not null, " +
                "age long,"+
                "name string(256),primary key(id))";
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    public SQLEvalResult execSql(String sql){
        try {
            ArrayList<BaseStatement> stats = evaluator.evaluate(sql);
            List<SQLEvalResult> results = stats.stream().map((stat) -> {
                stat.setSession(session);
                return stat.exec();
            }).collect(Collectors.toList());
            SQLEvalResult result = results.get(0);
            return result;
        } catch (Exception e){
            return new SQLEvalResult(e);
        }
    }

    String generateInsertSql(int id, long age, float charge, double salary,String name){
        String res = null;
        if(id==-1){
            // no primary key
            res = String.format("insert into %s (age, charge, salary, name) values" +
                    "(%d,%f,%f,'%s')",currentTableName,age,charge,salary,name);
        } else {
            res = String.format("insert into %s (id, age, charge, salary, name) values" +
                    "(%d,%d,%f,%f,'%s')", currentTableName, id, age, charge, salary, name);
        }
        System.out.println("Exec Sql: "+ res);
        return res;
    }

    @Test
    @Order(2)
    public void testInsert(){
        String sql = generateInsertSql(1,1,1,1,"alice");
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        sql = generateInsertSql(-1,1,1,1,"bob");
        result = execSql(sql);
        Assertions.assertTrue(result.error instanceof PrimaryKeyEmptyException);

        sql = generateInsertSql(2,1,1,1,"bob");
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into hi (id, age, name) values(1,2,'bob')";
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into hi (id, age, name) values(2,3,'alice')";
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(3)
    public void testUpdate(){
        String sql = "update hello set id = 3 where id = 1";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        // where and
        sql = "update hello set id = 1 where id = 3 and name = 'alice'";
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");

        // where or
        sql = "update hello set age = 2 where name = 'alice' or name = 'bob'";
        result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(4)
    public void testSelect(){
        // select id
        String sql = "select id from hello";
        SQLEvalResult result = execSql(sql);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(1).get(0),"2");

        // select age
        sql = "select age from hello";
        result = execSql(sql);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"2");
        Assertions.assertEquals(rows.get(1).get(0),"2");

        // select id, age, name from hello, hi on hello.name = hi.name
        sql = "select hello.id, hello.age, hello.name, hi.id from hello join hi on hello.name = hi.name";
        result = execSql(sql);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");
    }

    @Test
    @Order(5)
    public void testDelete(){
        String sql = "delete from hello where id = 2";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(6)
    public void testSelect2(){
        String sql = "select id from hello";
        SQLEvalResult result = execSql(sql);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
    }
}
