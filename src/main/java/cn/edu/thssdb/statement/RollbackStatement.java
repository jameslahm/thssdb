package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class RollbackStatement extends BaseStatement{
    private String savepoint_name;
    public RollbackStatement(){
        this.savepoint_name = null;
    }

    public RollbackStatement(String savepoint_name){
        this.savepoint_name = savepoint_name;
    }

    public String getSavepoint_name(){return savepoint_name;}

    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
