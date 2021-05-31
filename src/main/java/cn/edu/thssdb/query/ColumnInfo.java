package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.type.ColumnType;

public class ColumnInfo {
    private String field;
    private String type;
    private boolean primary_key;
    private boolean not_null;

    public ColumnInfo(String field,String type,boolean primary_key,boolean not_null){
        this.field = field;
        this.type = type;
        this.primary_key = primary_key;
        this.not_null = not_null;
    }

    public ColumnInfo(Column column){
        this.field = column.getName();
        this.type = ColumnType.toString(column.getType());
        this.primary_key = column.isPrimary();
        this.not_null = column.isNotNull();
    }

}
