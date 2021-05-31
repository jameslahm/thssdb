package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.SessionManager;

public class DropUserStatement extends BaseStatement{
    private String username;

    public DropUserStatement(String username){
        this.username = username;
    }

    @Override
    public SQLEvalResult exec(){
        SessionManager.getInstance().deleteUser(username);
        return new SQLEvalResult();
    }
}
