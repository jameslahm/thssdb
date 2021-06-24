package cn.edu.thssdb.statement;

import cn.edu.thssdb.exception.ColumnNotNullException;
import cn.edu.thssdb.exception.InsertColumnNotCorrException;
import cn.edu.thssdb.exception.PrimaryKeyEmptyException;
import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.ValueEntry;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.type.ColumnType;

import java.util.ArrayList;

public class InsertStatement extends BaseStatement{
    private static final long serialVersionUID = 1620487218622474542L;
    private ArrayList<String> column_names;
    private ArrayList<ValueEntry> values;
    private String table_name;
    private ArrayList<Row> insertedRows;

    public InsertStatement(String table_name,ArrayList<String> column_names,ArrayList<ValueEntry> values){
        this.table_name = table_name;
        this.column_names = column_names;
        this.values = values;
        this.insertedRows = new ArrayList<>();
    }

    public InsertStatement(String table_name,ArrayList<ValueEntry> values){
        this.table_name = table_name;
        this.column_names = null;
        this.values = values;
        this.insertedRows = new ArrayList<>();

    }

    @Override
    public SQLEvalResult exec(){
        Table table = getDatabase().getTableByName(table_name);
        int i = 0;
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        ArrayList<Row> rows = new ArrayList<>();
        ArrayList<Column> remains = new ArrayList<>();
        for (int j = 0; j < column_names.size(); j++) {
            indexs.add(table.columnToIndex(column_names.get(j)));
        }
        if (column_names.size() != table.columns.size()){
            for (Column column : table.columns){
                if (!column_names.contains(column.getName()))
                    remains.add(column);
            }
        }
        for (Column column:remains){
            if (column.isNotNull()){
                throw new PrimaryKeyEmptyException();
            }
        }

        while (i < values.size()) {
            Entry[] entries = new Entry[column_names.size()];
            ValueEntry temp_values = values.get(i);
            if (temp_values.values.size() != column_names.size()){
                throw new InsertColumnNotCorrException();
            }
            for (int j = 0; j < column_names.size(); j++) {
                Column column = table.columns.get(indexs.get(j));
                ColumnType type = column.getType();
                String value = temp_values.values.get(j);
                if (value.equalsIgnoreCase("null") && (column.isNotNull() || column.isPrimary())) {
                    throw new ColumnNotNullException();
                } else {
                    Entry x = new Entry(ColumnType.convertDataType(type, value));
                    entries[j] = x;
                }
            }

            i++;
            rows.add(new Row(entries));
        }
        insertedRows = new ArrayList<>();
        for (Row row:rows){
            insertedRows.add(row);
            table.insert(row);
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
        Table table = getDatabase().getTableByName(table_name);
        for (Row row:insertedRows){
            table.delete(row);
        }
    }
}
