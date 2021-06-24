package cn.edu.thssdb.statement;

import cn.edu.thssdb.exception.DatabaseNotExistException;
import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.SessionManager;

public class UseDatabaseStatement extends BaseStatement{

    private String database_name;

    public UseDatabaseStatement(String database_name){
        this.database_name = database_name;
    }

    public SQLEvalResult exec(){
        if(!Manager.getInstance().checkDatabaseExist(database_name)){
            throw new DatabaseNotExistException();
        };
        SessionManager.getInstance().getSessionById(session_id).UseDatabase(database_name);
        return new SQLEvalResult();
    }
}
