package cn.edu.thssdb.parser;

import cn.edu.thssdb.query.DatabasesInfo;
import cn.edu.thssdb.query.QueryResult;
import cn.edu.thssdb.query.TableInfo;
import cn.edu.thssdb.rpc.thrift.Status;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;
import java.util.List;

public class SQLEvalResult {
    public String message;
    public Exception error;
    public QueryResult queryResult;
    public DatabasesInfo databasesInfo;
    public ArrayList<TableInfo> tablesInfo;
    boolean showMeta;
    public SQLEvalResult() {
        this.error = null;
        this.queryResult = null;
        this.message = null;
    }

    public SQLEvalResult(Exception error) {
        this.error = error;
        this.message = null;
        this.queryResult = null;
    }

    public boolean onError() {
        return this.error != null;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatabasesInfo(DatabasesInfo databasesInfo){
        this.databasesInfo = databasesInfo;
    }

    public void setTablesInfo(ArrayList<TableInfo> tablesInfo){
        this.tablesInfo = tablesInfo;
    }

    public void setShowMeta(boolean b){this.showMeta = b;}
}
