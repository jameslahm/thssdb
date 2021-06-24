package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;

public class AlterAddStatement extends BaseStatement {
    String tableName;
    Column column;

    public AlterAddStatement(String tableName, Column column){
        this.tableName = tableName;
        this.column = column;
    }

    @Override
    public SQLEvalResult exec() {
        Table table =  this.database.getTableByName(tableName);
        table.addColumn(column);
        return new SQLEvalResult();
    }
}
