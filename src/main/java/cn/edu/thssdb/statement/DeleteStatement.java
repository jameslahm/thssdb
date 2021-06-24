package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.SessionManager;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;
import java.util.Iterator;

public class DeleteStatement extends BaseStatement{
    private String table_name;
    private Condition condition;
    private ArrayList<Row> deletedRows;
    public DeleteStatement(String table_name,Condition condition){
        this.table_name = table_name;
        this.condition = condition;
        this.deletedRows = new ArrayList<>();
    }

    public DeleteStatement(String table_name){
        this.table_name = table_name;
        this.condition = null;
        this.deletedRows = new ArrayList<>();
    }

    @Override
    public SQLEvalResult exec(){
        Table table = database.getTableByName(table_name);
        Iterator<Row> iter = table.iterator();
        if (this.condition == null){
            while(iter.hasNext()){
                Row row = iter.next();
                deletedRows.add(row);
                table.delete(row);
            }
        }
        else{
            while(iter.hasNext()){
                Row row = iter.next();
                if (condition.evaluate(row,table)){
                    deletedRows.add(row);
                    table.delete(row);
                }
            }
        }
        return new SQLEvalResult();
    }

    @Override
    public ArrayList<String> getTableNames(){
        ArrayList<String> tables = new ArrayList<>();
        tables.add(table_name);
        return tables;
    }

    @Override
    public void undo(){
        Table table = database.getTableByName(table_name);
        for (Row row:deletedRows){
            table.insert(row);
        }
    }
}
