package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;

public class TableInfo {
    private String tableName;
    private ArrayList<ColumnInfo> columnInfos;
    public TableInfo(String tableName, ArrayList<ColumnInfo> columnInfos){
        this.tableName = tableName;
        this.columnInfos = columnInfos;
    }

    public TableInfo(Table table){
        ArrayList<ColumnInfo> columnInfos = new ArrayList<>();
        for (Column column:table.columns){
            columnInfos.add(new ColumnInfo(column));
        }
        this.tableName = table.tableName;
        this.columnInfos = columnInfos;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ArrayList<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public void setColumnInfos(ArrayList<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }
}
