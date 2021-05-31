package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Database;
import cn.edu.thssdb.schema.Manager;

public class BaseStatement {
    public String username;
    public String database_name;
    public Database database;

    public void setSession(String username,String database_name){
        this.username = username;
        this.database_name = database_name;
        this.database = Manager.getInstance().getDatabases().get(database_name);
    }

    public SQLEvalResult exec(){
        return null;
    }
}
