package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;

public class TableInfo {
    private String table_name;
    private ArrayList<ColumnInfo> column_infos;
    public TableInfo(String table_name,ArrayList<ColumnInfo> column_infos){
        this.table_name = table_name;
        this.column_infos = column_infos;
    }

    public TableInfo(Table table){
        ArrayList<ColumnInfo> column_infos = new ArrayList<>();
        for (Column column:table.columns){
            column_infos.add(new ColumnInfo(column));
        }
        this.table_name = table.tableName;
        this.column_infos = column_infos;
    }
}
