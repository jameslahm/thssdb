package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;

public class AlterDropStatement extends BaseStatement {
    private static final long serialVersionUID = 4922016430330228790L;
    String tableName;
    String columnName;

    public AlterDropStatement(String tableName, String columnName){
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public SQLEvalResult exec() {
        Table table =  this.getDatabase().getTableByName(tableName);
        table.dropColumn(columnName);
        return new SQLEvalResult();
    }
}
