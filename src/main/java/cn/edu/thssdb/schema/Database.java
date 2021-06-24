package cn.edu.thssdb.schema;

import cn.edu.thssdb.exception.TableNotExistException;
import cn.edu.thssdb.query.QueryResult;
import cn.edu.thssdb.query.QueryTable;
import cn.edu.thssdb.statement.BaseStatement;
import cn.edu.thssdb.statement.CommitStatement;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Persist;
import javafx.scene.control.Tab;

import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import cn.edu.thssdb.exception.TableAlreadyExistException;

public class Database {

  private String name;
  private HashMap<String, Table> tables;
  ReentrantReadWriteLock lock;
  public Logger logger;
  private TransactionManager transactionManager;


  public Database(String name) {
    this.name = name;
    this.tables = new HashMap<>();
    this.lock = new ReentrantReadWriteLock();
    this.logger = new Logger("./log/" + name + ".json");
    this.transactionManager = new TransactionManager(this);
  }

  public Logger getLogger(){
    return logger;
  }

  public TransactionManager getTransactionManager(){
    return transactionManager;
  }

  public void persist() {
    tables.forEach((bleName,table)->{
      table.persist();
    });
    Persist.fromDatabaseMetaToJson(new ArrayList<>(tables.keySet()),getMetaPath());
    logger.redoList.clear();
    logger.writeLog();
  }

  public void create(String name, Column[] columns) {
    if(tables.containsKey(name)){
      throw new TableAlreadyExistException();
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
    tables.forEach((tableName,table)->{
      table.drop();
    });
    tables.clear();
  }
  public void deleteTable(String table_name){
    if(tables.containsKey(table_name)){
      tables.get(table_name).drop();
      tables.remove(table_name);
    }
  }

  private String getMetaPath(){
    return Paths.get(Global.DATA_FOLDER,name+".meta").toString();
  }

  public void recover() {
    ArrayList<String> tableNames = Persist.fromJsonToDatabaseMeta(getMetaPath());
    for (String tableName:tableNames){
      Table table = new Table(name,tableName);
      tables.put(tableName,table);
    }
    logger.readLog();
    ArrayList<BaseStatement> redoList = logger.getRedoList();
    ArrayList<BaseStatement> undoList;
    for (BaseStatement statement:redoList){
      statement.exec();
      if (statement instanceof CommitStatement){
        undoList = logger.getUndoList();
        for (int i = undoList.size() - 1;i >=0;i--){
          if (undoList.get(i).session_id == statement.session_id){
            undoList.remove(i);
          }
        }
      }
      else{
        logger.getUndoList().add(statement);
      }
    }
    undoList = logger.getUndoList();
    for (BaseStatement statement:undoList){
      statement.undo();
    }
  }

  public void quit() {
    persist();
  }
  public HashMap<String,Table> getTables() {return tables;}
  public Table getTableByName(String table_name) {
    if (this.tables.containsKey(table_name))
      return this.tables.get(table_name);
    else{
      throw new TableNotExistException();
    }
  }
}
