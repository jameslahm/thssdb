package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.Comparer;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.type.ColumnType;

import java.util.ArrayList;
import java.util.Iterator;

public class UpdateStatement extends BaseStatement{

    private String table_name;
    private String column_name;
    private Comparer value;
    private Condition condition;

    public UpdateStatement(String table_name,String column_name,Comparer value ,Condition condition){
        this.table_name = table_name;
        this.column_name = column_name;
        this.value = value;
        this.condition = condition;
    }

    @Override
    public SQLEvalResult exec(){
        Table table = database.getTableByName(table_name);
        int index = table.columnToIndex(column_name);
        if (index == -1){
            //TODO
            //throw exception
            return new SQLEvalResult();
        }
        Iterator<Row> iter = table.iterator();
        while(iter.hasNext()){
            Row row = iter.next();
            if (condition.evaluate(row,table)){
                ArrayList<Entry> entries = new ArrayList<>(row.getEntries());
                entries.set(index, new Entry(ColumnType.convertDataType(table.columns.get(index).getType(),value.value)));
                Row new_row = new Row((Entry[]) entries.toArray());
                table.update(new_row,row);
            }
        }
        return new SQLEvalResult();
    }
}
