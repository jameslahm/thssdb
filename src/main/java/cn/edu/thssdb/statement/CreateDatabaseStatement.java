package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Manager;

public class CreateDatabaseStatement extends BaseStatement{
    private String database_name;
    public CreateDatabaseStatement(String db_name){
        this.database_name = db_name;
    }
    @Override
    public SQLEvalResult exec(){
        Manager manager = Manager.getInstance();
        manager.createDatabaseIfNotExists(this.database_name);
        return new SQLEvalResult();
    }
}
