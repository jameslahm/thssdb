package cn.edu.thssdb.schema;

import cn.edu.thssdb.server.ThssDB;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Persist;

import javax.xml.bind.annotation.XmlElementDecl;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Manager {
  private HashMap<String, Database> databases;
  private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private HashMap<String,Integer> onlineDatabase;

  public static Manager getInstance() {
    return Manager.ManagerHolder.INSTANCE;
  }

  public Manager() {
    // TODO
    this.databases = new HashMap<>();
    init();
  }

  private String getMetaPath(){
    return Paths.get(Global.DATA_FOLDER,"manager.meta").toString();
  }

  private void init(){
    ArrayList<String> databaseNames = Persist.fromJsonToManagerMeta(getMetaPath());
    for (String databaseName:databaseNames){
      Database database = new Database(databaseName);
      databases.put(databaseName,database);
      onlineDatabase.put(databaseName,0);
    }
  }

  public void createDatabaseIfNotExists(String databaseName) {
    // TODO
    if (databases.containsKey(databaseName)) {
      return;
    } else {
      Database database = new Database(databaseName);
      databases.put(databaseName,database);
    }
  }

  public void deleteDatabase(String databaseName) {
    // TODO
    if(databases.containsKey(databaseName)){
      Database database = databases.get(databaseName);
      database.drop();
      databases.remove(databaseName);
    }
  }

  public void switchDatabase(String databaseName) {
    // TODO
    Integer clientNum = onlineDatabase.get(databaseName);
    onlineDatabase.put(databaseName,clientNum+1);
  }

  public HashMap<String,Database> getDatabases(){return this.databases;}
  public void persist(){
    databases.forEach((databaseName,database)->{
      database.persist();
    });
    Persist.fromManagerMetaToJson(new ArrayList<>(databases.keySet()),getMetaPath());
  }

  private static class ManagerHolder {
    private static final Manager INSTANCE = new Manager();
    private ManagerHolder() {
    }
  }
}
