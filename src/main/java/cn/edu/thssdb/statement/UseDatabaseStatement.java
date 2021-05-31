package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.SessionManager;

public class UseDatabaseStatement extends BaseStatement{

    private String database_name;

    public UseDatabaseStatement(String database_name){
        this.database_name = database_name;
    }

    public SQLEvalResult exec(int session_id){
        SessionManager.getInstance().getSessionById(session_id).UseDatabase(String database_name);
        return new SQLEvalResult();
    }
}
