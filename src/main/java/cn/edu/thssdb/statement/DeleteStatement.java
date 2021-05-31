package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.SessionManager;
import cn.edu.thssdb.schema.Table;

import java.util.Iterator;

public class DeleteStatement extends BaseStatement{
    private String table_name;
    private Condition condition;
    private Table table;

    public DeleteStatement(String table_name,Condition condition){
        this.table_name = table_name;
        this.condition = condition;
    }

    public DeleteStatement(String table_name){
        this.table_name = table_name;
        this.condition = null;
    }

    @Override
    public SQLEvalResult exec(){
        Table table = database.getTableByName(table_name);
        Iterator<Row> iter = table.iterator();
        if (this.condition == null){
            while(iter.hasNext()){
                Row row = iter.next();
                table.delete(row);
            }
        }
        else{
            while(iter.hasNext()){
                Row row = iter.next();
                if (condition.evaluate(row,table)){
                    table.delete(row);
                }
            }
        }
        return new SQLEvalResult();
    }
}
