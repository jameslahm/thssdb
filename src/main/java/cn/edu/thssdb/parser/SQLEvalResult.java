package cn.edu.thssdb.parser;

import cn.edu.thssdb.query.DatabasesInfo;
import cn.edu.thssdb.query.QueryResult;
import cn.edu.thssdb.query.TableInfo;
import cn.edu.thssdb.rpc.thrift.Status;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.utils.Cell;

import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.message = "ok";
    }

    public SQLEvalResult(Exception error) {
        this.error = error;
        this.message = error.getMessage();
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
        // transform table info to query result
        this.tablesInfo = tablesInfo;
        List<Row> rows = tablesInfo.stream().map(tableInfo -> {
            return new Row(List.of( new Entry(tableInfo.getTableName())).toArray(new Entry[0]));
        }).collect(Collectors.toList());
        List<Cell> attrs = List.of(new Cell("Tables"));
        this.queryResult = new QueryResult(rows,attrs);
    }

    public void setShowMeta(boolean b){this.showMeta = b;}
}
