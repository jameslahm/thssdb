package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.schema.Column;

public class CreateTableStatement extends BaseStatement {
    private static final long serialVersionUID = -207307759707911805L;
    private String table_name;
    private Column[] columns;

    public CreateTableStatement(String table_name, Column[] columns){
        this.table_name = table_name;
        this.columns = columns;
    }
    @Override
    public SQLEvalResult exec(){
        this.getDatabase().create(table_name,columns);
        return new SQLEvalResult();
    }

    @Override
    public void undo() {
        this.getDatabase().deleteTable(this.table_name);
    }
}
