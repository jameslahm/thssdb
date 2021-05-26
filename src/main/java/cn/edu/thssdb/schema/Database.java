package cn.edu.thssdb.schema;

import cn.edu.thssdb.query.QueryResult;
import cn.edu.thssdb.query.QueryTable;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Persist;
import javafx.scene.control.Tab;

import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {

  private String name;
  private HashMap<String, Table> tables;
  ReentrantReadWriteLock lock;

  public Database(String name) {
    this.name = name;
    this.tables = new HashMap<>();
    this.lock = new ReentrantReadWriteLock();
    recover();
  }

  public void persist() {
    // TODO
    tables.forEach((bleName,table)->{
      table.persist();
    });
    Persist.fromDatabaseMetaToJson(new ArrayList<>(tables.keySet()),getMetaPath());
  }

  public void create(String name, Column[] columns) {
    // TODO
    if(tables.containsKey(name)){
      return;
    } else {
      Table table = new Table(this.name,name,columns);
      tables.put(name,table);
    }
  }

  public void delete(String name){
    Table table =  tables.get(name);
    if(table!=null){
      table.drop();
      tables.remove(name);
    }
  }

  public void drop() {
    // TODO
    tables.forEach((tableName,table)->{
      table.drop();
    });
    tables.clear();
  }

  public String select(QueryTable[] queryTables) {
    // TODO
    QueryResult queryResult = new QueryResult(queryTables);
    return null;
  }

  private String getMetaPath(){
    return Paths.get(Global.DATA_FOLDER,name+".meta").toString();
  }

  private void recover() {
    // TODO
    ArrayList<String> tableNames = Persist.fromJsonToDatabaseMeta(getMetaPath());
    for (String tableName:tableNames){
      Table table = new Table(name,tableName);
      tables.put(tableName,table);
    }
  }

  public void quit() {
    // TODO
    persist();
  }
}
