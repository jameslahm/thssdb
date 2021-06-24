package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class CommitStatement extends BaseStatement{
    private static final long serialVersionUID = 1816665808316896846L;

    public CommitStatement(){

    }

    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
