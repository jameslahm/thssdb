package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Database;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseStatement implements Serializable {
    public String databaseName;
    public long session_id = -1;
    public void setSession(Session session){
        this.databaseName = session.getCurrentDatabaseName();
        this.session_id = session.getSessionId();
    }

    public Database getDatabase(){
        HashMap<String, Database> databases =  Manager.getInstance().getDatabases();
        return databases.get(databaseName);
    }

    public SQLEvalResult exec(){
        return null;
    }

    public BaseStatement getOppositeStatement(){
        return null;
    }

    public ArrayList<String> getTableNames(){
        return new ArrayList<>();
    }
    public void undo(){

    }
}
