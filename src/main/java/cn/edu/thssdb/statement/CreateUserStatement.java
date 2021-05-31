package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.SessionManager;
public class CreateUserStatement extends BaseStatement {
    private String username;
    private String password;

    public CreateUserStatement(String username,String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public SQLEvalResult exec(){
        SessionManager.getInstance().create_user(username,password);
        return new SQLEvalResult();
    }

}
