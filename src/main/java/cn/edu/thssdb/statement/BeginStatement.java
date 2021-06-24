package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class BeginStatement extends BaseStatement{

    private static final long serialVersionUID = -872545283712092463L;

    public BeginStatement(){

    }
    @Override
    public SQLEvalResult exec(){
        return new SQLEvalResult();
    }
}
