package cn.edu.thssdb.parser;

import cn.edu.thssdb.schema.*;
import cn.edu.thssdb.statement.BaseStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
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
        String sql = "create table hello (id int)";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    public SQLEvalResult execSql(String sql){
        ArrayList<BaseStatement> stats = evaluator.evaluate(sql);
        List<SQLEvalResult> results = stats.stream().map((stat)-> {
            stat.setSession(session);
            return stat.exec();
        }).collect(Collectors.toList());
        SQLEvalResult  result = results.get(0);
        return result;
    }

    @Test
    @Order(2)
    public void testInsert(){
        String sql = "insert into hello (id) values(1)";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(3)
    public void testUpdate(){
        String sql = "update hello set id = 2 where id = 1";
        SQLEvalResult result = execSql(sql);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(4)
    public void testSelect(){
        String sql = "select id from hello";
        SQLEvalResult result = execSql(sql);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
        Assertions.assertEquals(rows.get(0).size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"2");
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
        Assertions.assertEquals(rows.size(),0);
    }
}
