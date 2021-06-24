package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;

import java.util.ArrayList;

public class DropTableStatement extends BaseStatement{
    private String table_name;

    public DropTableStatement(String table_name){
        this.table_name = table_name;
    }

    @Override
    public SQLEvalResult exec(){
        this.getDatabase().deleteTable(table_name);
        return new SQLEvalResult();
    }

    @Override
    public ArrayList<String> getTableNames(){
        ArrayList<String> tables = new ArrayList<>();
        tables.add(table_name);
        return tables;
    }

}
