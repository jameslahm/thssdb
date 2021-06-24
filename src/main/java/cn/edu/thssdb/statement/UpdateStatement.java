package cn.edu.thssdb.statement;

import cn.edu.thssdb.exception.ColumnNotExistException;
import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.Comparer;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.type.ColumnType;
import cn.edu.thssdb.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;

public class UpdateStatement extends BaseStatement{

    private static final long serialVersionUID = 7627090398151756604L;
    private String table_name;
    private String column_name;
    private Comparer value;
    private Condition condition;
    private ArrayList<Pair<Row,Row>> updatedRowPairs;
    public UpdateStatement(String table_name,String column_name,Comparer value ,Condition condition){
        this.table_name = table_name;
        this.column_name = column_name;
        this.value = value;
        this.condition = condition;
        this.updatedRowPairs = new ArrayList<>();
    }

    @Override
    public SQLEvalResult exec(){
        updatedRowPairs = new ArrayList<>();
        Table table = getDatabase().getTableByName(table_name);
        int index = table.columnToIndex(column_name);
        if (index == -1){
            throw new ColumnNotExistException();
        }
        Iterator<Row> iter = table.iterator();
        while(iter.hasNext()){
            Row row = iter.next();
            if (condition==null || condition.evaluate(row,table)){
                ArrayList<Entry> entries = new ArrayList<>(row.getEntries());
                entries.set(index, new Entry(ColumnType.convertDataType(table.columns.get(index).getType(),value.value)));
                Row new_row = new Row(entries.toArray(new Entry[0]));
                updatedRowPairs.add(new Pair<>(row,new_row));
                table.update(new_row,row);
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
        Table table =getDatabase().getTableByName(table_name);
        for (int i = updatedRowPairs.size() -1;i >= 0;i--){
            table.update(updatedRowPairs.get(i).left,updatedRowPairs.get(i).right);
        }
    }
}
