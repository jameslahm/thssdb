package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class CommitStatement extends BaseStatement{
    public long session_id = -1;
    public CommitStatement(){

    }

    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
