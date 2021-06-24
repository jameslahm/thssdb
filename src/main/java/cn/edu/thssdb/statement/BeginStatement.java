package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class BeginStatement extends BaseStatement{

    public BeginStatement(){

    }
    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
