package cn.edu.thssdb.parser;

import cn.edu.thssdb.exception.DatabaseNotExistException;
import cn.edu.thssdb.exception.PrimaryKeyEmptyException;
import cn.edu.thssdb.schema.*;
import cn.edu.thssdb.statement.BaseStatement;
import cn.edu.thssdb.statement.UseDatabaseStatement;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        SessionManager.getInstance().addSession(session);
        evaluator = new SQLEvaluator(manager,session);
        manager.createDatabaseIfNotExists(currentDatabaseName);
    }

    @Test
    @Order(1)
    public void testCreateTable(){
        String sql = "create table hello (id int not null, " +
                "age long, charge float, salary double " +
                ", name string(256),primary key(id))";
        SQLEvalResult result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "create table hi (id int not null, " +
                "age long,"+
                "name string(256),primary key(id))";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "create table yello (identifier int not null, "+
                "name string(256),"+
                "primary key(identifier))";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");
    }

    public SQLEvalResult execSql(String sql, Session session1){
        System.out.println(sql);
        try {
            ArrayList<BaseStatement> stats = evaluator.evaluate(sql);
            List<SQLEvalResult> results = stats.stream().map((stat) -> {
                if(session1==null){
                    stat.setSession(session);
                } else {
                    stat.setSession(session1);
                }
                Database database = Manager.getInstance().getDatabaseByName(session.getCurrentDatabaseName());
                if (database==null && !(stat instanceof UseDatabaseStatement)){
                    SQLEvalResult result = new SQLEvalResult();
                    result.error = new DatabaseNotExistException();
                    return result;
                } else {
                    if(stat instanceof UseDatabaseStatement){
                        try{
                            stat.exec();
                            return new SQLEvalResult();
                        } catch (Exception e){
                            SQLEvalResult result = new SQLEvalResult();
                            result.error = e;
                            return result;
                        }
                    }
                    try {
                        Pair<SQLEvalResult, Boolean> p = database.getTransactionManager().exec(stat);
                        return p.left;
                    } catch (Exception e){
                        SQLEvalResult result = new SQLEvalResult();
                        result.error = e;
                        return result;
                    }
                }
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
        return res;
    }

    @Test
    @Order(2)
    public void testInsert(){
        String sql = generateInsertSql(1,1,1,1,"alice");
        SQLEvalResult result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = generateInsertSql(-1,1,1,1,"bob");
        result = execSql(sql,session);
        Assertions.assertTrue(result.error instanceof PrimaryKeyEmptyException);

        sql = generateInsertSql(2,1,1,1,"bob");
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into hi (id, age, name) values(1,2,'bob')";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into hi (id, age, name) values(2,3,'alice')";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into hi (id, age, name) values(3,4,'cindy')";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message, "ok");

        sql = "insert into yello (identifier, name) values(1,'alice'),(2,'flank') ";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(3)
    public void testUpdate(){
        String sql = "update hello set id = 3 where id = 1";
        SQLEvalResult result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        // where and
        sql = "update hello set id = 1 where id = 3 and name = 'alice'";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        // where or
        sql = "update hello set age = 2 where name = 'alice' or name = 'bob'";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(4)
    public void testSelect(){
        // select id
        String sql = "select id from hello";
        SQLEvalResult result = execSql(sql,session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(1).get(0),"2");

        // select age
        sql = "select age from hello";
        result = execSql(sql,session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"2");
        Assertions.assertEquals(rows.get(1).get(0),"2");

        // select id, age, name from hello join hi on hello.name = hi.name
        sql = "select hello.id, hello.age, hello.name, hi.id from hello join hi on hello.name = hi.name";
        result = execSql(sql,session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");

        // select id, age, name from hello, hi where hello.name = hi.name
        sql = "select hello.id, hello.age, hello.name, hi.id from hello, hi where hello.name = hi.name";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");

        // select id, age, name from hello left outer join hi on hello.name = hi.name
        sql = "select hello.id, hello.age, hello.name, hi.id from hello left outer join hi on hello.name = hi.name";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),2);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");

        // select id, age, name from hello right outer join hi on hello.name = hi.name
        sql = "select hello.id, hello.age, hello.name, hi.id from hello right outer join hi on hello.name = hi.name";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),3);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");

        // select id, name from hello natural join yello
        sql = "select hello.id, hello.age, hello.name, yello.identifier from hello natural join yello";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"1");
        Assertions.assertEquals(rows.get(0).get(2),"alice");

        sql = "select hello.id from hello,hi,yello where hello.name = hi.name";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),4);
    }

    @Test
    @Order(5)
    public void testDelete(){
        String sql = "delete from hello where id = 2";
        SQLEvalResult result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(6)
    public void testSelect2(){
        String sql = "select id from hello";
        SQLEvalResult result = execSql(sql, session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
    }

    @Test
    @Order(7)
    public void testAlter(){
        String sql = "alter table hello add yes int";
        SQLEvalResult result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "select * from hello";
        result = execSql(sql, session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.get(0).size(),6);

        sql = "alter table hello drop column yes";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "select * from hello";
        result = execSql(sql, session);
        rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.get(0).size(),5);
    }

    @Test
    @Order(8)
    public void testCreateDatabase(){
        String sql = "create database course";
        SQLEvalResult result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "use course";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");
    }

    @Test
    @Order(9)
    public void testDropDatabase(){
        String sql = "drop database course";
        SQLEvalResult result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "use test";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "use course";
        result = execSql(sql, session);
        Assertions.assertTrue(result.error instanceof DatabaseNotExistException);
    }

    @Test
    @Order(10)
    public void testRollback(){
        String sql = "create table student (id int, age int, primary key(id))";
        SQLEvalResult result =  execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "begin transaction";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(2,1);";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "rollback";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "select id,age from student";
        result = execSql(sql, session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),0);
    }

    @Test
    @Order(11)
    public void testRollbackSavePoint(){
        String sql = "begin transaction";
        SQLEvalResult result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(2,1);";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "savepoint hello";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(4,1)";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "rollback to savepoint hello";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "commit";
        result = execSql(sql, session);
        Assertions.assertEquals(result.message,"ok");

        sql = "select id,age from student";
        result = execSql(sql, session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
    }

    @Test
    @Order(12)
    public void testReadCommitted() throws InterruptedException {
        Global.DATABASE_ISOLATION_LEVEL = Global.ISOLATION_LEVEL.READ_COMMITTED;
        Session session1 = new Session(2,currentDatabaseName);
        SessionManager.getInstance().addSession(session1);
        Runnable runnable = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");

            sql = "insert into student(id,age) values(1,1)";
            result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sql = "commit";
            result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");
        };
        Runnable runnable1 = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session1);
            Assertions.assertEquals(result.message,"ok");

            long start =  System.currentTimeMillis();
            sql = "select id,age from student;";
            result = execSql(sql, session1);
            long end  = System.currentTimeMillis();
            Assertions.assertTrue((end-start)>1000);
            List<List<String>> rows = result.queryResult.rowsToString();
            Assertions.assertEquals(rows.size(),2);
        };

        new Thread(runnable).start();
        new Thread(runnable1).start();
        Thread.sleep(5000);
    }

    @Test
    @Order(13)
    public void testReadUnCommitted() throws InterruptedException {
        Global.DATABASE_ISOLATION_LEVEL = Global.ISOLATION_LEVEL.READ_UNCOMMITTED;
        Session session1 = new Session(2,currentDatabaseName);
        SessionManager.getInstance().addSession(session1);
        Runnable runnable = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");

            sql = "insert into student(id,age) values(3,1)";
            result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sql = "commit";
            result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");
        };
        Runnable runnable1 = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session1);
            Assertions.assertEquals(result.message,"ok");

            long start =  System.currentTimeMillis();
            sql = "select id,age from student;";
            result = execSql(sql, session1);
            long end  = System.currentTimeMillis();
            Assertions.assertTrue((end-start)<1000);
            List<List<String>> rows = result.queryResult.rowsToString();
            Assertions.assertEquals(rows.size(),3);
        };

        new Thread(runnable).start();
        new Thread(runnable1).start();
        Thread.sleep(5000);
    }

    @Test
    @Order(14)
    public void testSerializable() throws InterruptedException {
        Global.DATABASE_ISOLATION_LEVEL = Global.ISOLATION_LEVEL.SERIALIZABLE;
        Session session1 = new Session(2,currentDatabaseName);
        SessionManager.getInstance().addSession(session1);
        Runnable runnable = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");

            sql = "select id,age from student;";
            result = execSql(sql, session);
            List<List<String>> rows = result.queryResult.rowsToString();
            Assertions.assertEquals(rows.size(),3);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sql = "select id,age from student;";
            result = execSql(sql, session);
            rows = result.queryResult.rowsToString();
            Assertions.assertEquals(rows.size(),3);


            sql = "commit";
            result = execSql(sql, session);
            Assertions.assertEquals(result.message,"ok");
        };
        Runnable runnable1 = () -> {
            String sql = "begin transaction";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SQLEvalResult result = execSql(sql, session1);
            Assertions.assertEquals(result.message,"ok");

            long start =  System.currentTimeMillis();
            sql = "insert into student(id,age) values(4,1);";
            result = execSql(sql, session1);
            long end  = System.currentTimeMillis();
            Assertions.assertTrue((end-start)>1000);

            sql = "commit";
            result = execSql(sql, session1);
            Assertions.assertEquals(result.message,"ok");
        };

        new Thread(runnable).start();
        new Thread(runnable1).start();
        Thread.sleep(5000);
    }

    @Test
    @Order(15)
    public void testRestore(){
        Database database = Manager.getInstance().getDatabaseByName(currentDatabaseName);
        database.deleteTable("student");
        database.logger.redoList.clear();

        String sql = "begin transaction";
        SQLEvalResult result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "create table student (id int, age int, primary key(id))";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(5,1)";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "commit";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "begin transaction";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(6,1)";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        manager.init();

        sql = "select id from student";
        result = execSql(sql,session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"5");
    }

    @Test
    @Order(16)
    public void testCheckpoint(){
        Database database = Manager.getInstance().getDatabaseByName(currentDatabaseName);
        database.deleteTable("student");
        database.logger.redoList.clear();

        String sql = "begin transaction";
        SQLEvalResult result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "create table student (id int, age int, primary key(id))";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(5,1)";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "checkpoint";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "begin transaction";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        sql = "insert into student(id,age) values(6,1)";
        result = execSql(sql,session);
        Assertions.assertEquals(result.message,"ok");

        manager.init();

        sql = "select id from student";
        result = execSql(sql,session);
        List<List<String>> rows = result.queryResult.rowsToString();
        Assertions.assertEquals(rows.size(),1);
        Assertions.assertEquals(rows.get(0).get(0),"5");
    }

}
