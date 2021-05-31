package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.query.DatabasesInfo;
import cn.edu.thssdb.schema.Database;
import cn.edu.thssdb.schema.Manager;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDatabaseStatement extends BaseStatement{

    @Override
    public SQLEvalResult exec(){
        HashMap<String, Database> databases = Manager.getInstance().getDatabases();
        ArrayList<String> database_names = new ArrayList<>(databases.keySet());
        DatabasesInfo databasesInfo = new DatabasesInfo(database_names);
        SQLEvalResult result = new SQLEvalResult();
        result.setDatabasesInfo(databasesInfo);
        return result;
    }
}
