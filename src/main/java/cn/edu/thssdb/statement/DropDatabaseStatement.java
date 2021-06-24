package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Database;
import cn.edu.thssdb.schema.Manager;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;
import java.util.HashMap;

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
    @Override
    public ArrayList<String> getTableNames(){
        ArrayList<String> tableNames = new ArrayList<>();
        Database database = Manager.getInstance().getDatabases().get(database_name);
        HashMap<String, Table> tables = database.getTables();
        for (String table:tables.keySet()){
            tableNames.add(table);
        }
        return tableNames;
    }
}
