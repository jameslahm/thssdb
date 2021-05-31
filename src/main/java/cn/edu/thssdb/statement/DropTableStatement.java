package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

public class DropTableStatement extends BaseStatement{
    private String table_name;

    public DropTableStatement(String table_name){
        this.table_name = table_name;
    }

    @Override
    public SQLEvalResult exec(){
        this.database.deleteTable(table_name);
        return new SQLEvalResult();
    }
}
