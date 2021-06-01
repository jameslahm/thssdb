package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Database;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.Session;

public class BaseStatement {
    public String databaseName;
    public Database database;

    public void setSession(Session session){
        this.databaseName = session.getCurrentDatabaseName();
        this.database = Manager.getInstance().getDatabases().get(databaseName);
    }

    public SQLEvalResult exec(){
        return null;
    }
}
