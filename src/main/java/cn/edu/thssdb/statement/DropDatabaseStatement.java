package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Manager;

public class DropDatabaseStatement extends BaseStatement{
    private String database_name;

    public DropDatabaseStatement(String db_name){
        database_name = db_name;
    }

    @Override
    public SQLEvalResult exec(){
        Manager.getInstance().deleteDatabase(database_name);
        return new SQLEvalResult();
    }
}
