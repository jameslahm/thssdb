package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class SavepointStatement extends BaseStatement{
    private String savepoint_name;
    public SavepointStatement(String savepoint_name){
        this.savepoint_name = savepoint_name;
    }

    public String getSavepoint_name() {
        return savepoint_name;
    }

    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
