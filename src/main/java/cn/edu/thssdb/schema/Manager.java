package cn.edu.thssdb.schema;

import cn.edu.thssdb.server.ThssDB;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Persist;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Manager {
  private HashMap<String, Database> databases = new HashMap<>();
  private static ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
  private HashMap<String,Integer> onlineDatabase = new HashMap<>();

  public static Manager getInstance() {
    return Manager.ManagerHolder.INSTANCE;
  }

  public void setDatabases(HashMap<String, Database> databases) {
    this.databases = databases;
  }

  public static ReentrantReadWriteLock getMutex() {
    return mutex;
  }

  public HashMap<String, Integer> getOnlineDatabase() {
    return onlineDatabase;
  }

  public Manager() {
    this.databases = new HashMap<>();
  }

  public Database  getDatabaseByName(String databaseName){
    return databases.get(databaseName);
  }

  private String getMetaPath(){
    return Paths.get(Global.DATA_FOLDER,"manager.meta").toString();
  }

  public void init(){
    ArrayList<String> databaseNames = Persist.fromJsonToManagerMeta(getMetaPath());
    for (String databaseName:databaseNames){
      Database database = new Database(databaseName);
      databases.put(databaseName,database);
      database.recover();
      onlineDatabase.put(databaseName,0);
    }
  }

  public void createDatabaseIfNotExists(String databaseName) {
    if (databases.containsKey(databaseName)) {
      return;
    } else {
      Database database = new Database(databaseName);
      databases.put(databaseName,database);
    }
  }

  public boolean checkDatabaseExist(String databaseName){
    return databases.containsKey(databaseName);
  }


  public void deleteDatabase(String databaseName) {
    if(databases.containsKey(databaseName)){
      Database database = databases.get(databaseName);
      database.drop();
      databases.remove(databaseName);
    }
  }

  public void switchDatabase(String databaseName) {
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

  public static class ManagerHolder {
    public static final Manager INSTANCE = new Manager();
    public ManagerHolder() {
    }
  }
}
