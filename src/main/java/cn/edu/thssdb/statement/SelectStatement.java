package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.items.Condition;
import cn.edu.thssdb.parser.items.TableQuery;
import cn.edu.thssdb.query.QueryResult;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.utils.Cell;
import cn.edu.thssdb.utils.Pair;

import java.util.ArrayList;

public class SelectStatement extends BaseStatement{

    private ArrayList<String> result_columns;
    private Condition condition;
    private ArrayList<TableQuery> table_queries;
    private ArrayList<Table> tables;

    public SelectStatement(ArrayList<String> result_columns,ArrayList<TableQuery> table_queries,Condition condition){
        this.result_columns = result_columns;
        this.table_queries = table_queries;
        this.condition = condition;
        this.tables = new ArrayList<>();
    }

    public SelectStatement(ArrayList<String> result_columns,ArrayList<TableQuery> table_queries){
        this.result_columns = result_columns;
        this.table_queries = table_queries;
        this.condition = null;
        this.tables = new ArrayList<>();
    }

    public void generate_tables(){
        for (TableQuery query:table_queries){
            ArrayList<Table> temp_tables = query.generateTables(this.database);
            this.tables.addAll(temp_tables);
        }
    }

    public static Row cartesian_product(ArrayList<ArrayList<Row>> table_rows, ArrayList<Integer> indexs)
    {
        ArrayList<Row> rows = new ArrayList<>();
        for (int i =0;i<indexs.size();i++){
            rows.add(table_rows.get(i).get(indexs.get(i)));
        }
        return TableQuery.combine_rows_wo_condition(rows);
    }
    public static Pair<Integer,Integer> getIndexFromTables(ArrayList<Table> tables,String column_fullname){
        int table_index;
        int column_index;
        String[] parts = column_fullname.split("[.]");
        if(parts.length == 1){
            for(int i =0;i<tables.size();i++){
                column_index = tables.get(i).columnToIndex(column_fullname);
                if (column_index != -1){
                    table_index = i;
                    return new Pair<>(table_index,column_index);
                }
            }
        }
        else{
            String table_name = parts[0];
            String column_name = parts[1];
            for(int i =0;i<tables.size();i++){
                if (tables.get(i).tableName.equalsIgnoreCase(table_name)){
                    column_index = tables.get(i).columnToIndex(column_name);
                    if (column_index != -1){
                        table_index = i;
                        return new Pair<>(table_index,column_index);
                    }
                }
            }
        }
        // TODO
        // throw exception
        throw new RuntimeException();
    }

    @Override
    public SQLEvalResult exec(){
        this.generate_tables();
        int column_size = result_columns.size();
        boolean select_all = false;
        ArrayList<Row> select_rows = new ArrayList<>();
        ArrayList<Cell> select_cells = new ArrayList<>();

        for(int i = 0;i<column_size;i++){
            String name = result_columns.get(i);
            if (name.equals("*")){
                select_all = true;
                break;
            }
            else if(name.endsWith("*")){
                String[] parts = name.split("[.]");
                String table_name = parts[0];
                String column_name =parts[1];
                boolean find = false;
                for (Table table:tables){
                    if (table.tableName.equalsIgnoreCase(table_name)){
                        for (Column column:table.columns){
                            result_columns.add(column.getName());
                        }
                        find = true;
                        break;
                    }
                }
                if (!find){
                    //TODO
                    throw new RuntimeException();
                }
            }
        }

        for(String name:result_columns){
            select_cells.add(new Cell(name));
        }

        ArrayList<ArrayList<Row>> query_table_rows = new ArrayList<>();
        for (TableQuery query:table_queries){
            query_table_rows.add(query.handle_join());
        }

        ArrayList<Integer> query_table_sizes = new ArrayList<>();
        ArrayList<Integer> table_locs = new ArrayList<>();
        Integer total_size = 1;
        for (int i =0;i<query_table_rows.size();i++){
            ArrayList<Row> rows = query_table_rows.get(i);
            query_table_sizes.add(rows.size());
            total_size *= rows.size();
        }
        for (int i = 0;i<tables.size();i++){
            if (table_locs.isEmpty())
                table_locs.add(0);
            else {
                Table table = tables.get(i-1);
                table_locs.add(table_locs.get(i - 1) + table.columns.size());
            }
        }

        ArrayList<Integer> indexs = new ArrayList<>();
        for (int j = query_table_sizes.size() - 1;j >=0;j--){
            indexs.add(0);
        }

        for (int i =0;i<total_size;i++){
            int temp = i;
            for (int j = query_table_sizes.size() - 1;j >=0;j--){
                indexs.set(j,temp % query_table_sizes.get(j));
                temp = temp / query_table_sizes.get(j);
            }
            Row row = cartesian_product(query_table_rows,indexs);
            if(condition==null || condition.evaluate(row,tables,table_locs)){
                if (select_all)
                    select_rows.add(row);
                else {
                    ArrayList<Entry> entries = new ArrayList<>();
                    for(String name:result_columns){
                        Pair<Integer,Integer> pair = getIndexFromTables(tables,name);
                        Entry entry = row.getEntries().get(table_locs.get(pair.left) + pair.right);
                        entries.add(entry);
                    }
                    select_rows.add(new Row(entries.toArray(new Entry[0])));
                }
            }
        }

        SQLEvalResult result = new SQLEvalResult();
        result.setQueryResult(new QueryResult(select_rows,select_cells));
        return result;
    }

}
