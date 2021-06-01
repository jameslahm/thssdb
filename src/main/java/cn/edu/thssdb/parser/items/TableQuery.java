package cn.edu.thssdb.parser.items;

import cn.edu.thssdb.query.QueryTable;
import cn.edu.thssdb.schema.*;
import cn.edu.thssdb.type.CompareType;
import cn.edu.thssdb.type.JoinType;
import cn.edu.thssdb.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;

public class TableQuery {
    TableQuery left_join;
    TableQuery right_join;
    String table_name;
    JoinType join_type;
    Condition on_condition;
    ArrayList<Table> tables;
//    ArrayList<String> query_columns;

    public TableQuery(TableQuery left_join,TableQuery right_join,String join_type,Condition on_condition){
        this.table_name = null;
        this.left_join = left_join;
        this.right_join = right_join;
        this.on_condition = on_condition;
        if (join_type.equalsIgnoreCase("natural")) this.join_type = JoinType.NATURAL;
        if (join_type.equalsIgnoreCase("inner")) this.join_type = JoinType.INNER;
        if (join_type.equalsIgnoreCase("left outer")) this.join_type = JoinType.LEFT_OUTER;
        if (join_type.equalsIgnoreCase("right outer")) this.join_type = JoinType.RIGHT_OUTER;
        if (join_type.equalsIgnoreCase("full outer")) this.join_type = JoinType.FULL_OUTER;
    }

    public TableQuery(String table_name){
        this.table_name = table_name;
        this.left_join = null;
        this.right_join = null;
        this.join_type = null;
        this.on_condition = null;
    }

    public ArrayList<Table> generateTables(Database database){
        if (this.table_name == null){
            ArrayList<Table> left = new ArrayList<Table>(left_join.generateTables(database));
            ArrayList<Table> right = new ArrayList<Table>(right_join.generateTables(database));
            left.addAll(right);
            this.tables = left;
            return left;
        }
        else{
            ArrayList<Table> res = new ArrayList<>();
            res.add(database.getTableByName(this.table_name));
            this.tables = res;
            return res;
        }
    }

//    public void propagateQuery_columns(ArrayList<String> column_names){
//        if (this.left_join != null){
//            left_join.propagateQuery_columns(column_names);
//            right_join.propagateQuery_columns(column_names);
//        }
//        this.query_columns = column_names;
//    }
    public ArrayList<Pair<String,String>> search_same_columns(){
        ArrayList<Pair<String,String>> results = new ArrayList<>();
        ArrayList<Pair<String,String>> left = new ArrayList<>();
        ArrayList<Pair<String,String>> right = new ArrayList<>();
        // assume left tables do not have two column with same name
        for(Table table:left_join.tables){
            for (Column column:table.columns){
                left.add(new Pair<>(table.tableName, column.getName()));
            }
        }
        // assume right tables do not have two column with same name
        for(Table table: right_join.tables){
            for (Column column:table.columns){
                right.add(new Pair<>(table.tableName,column.getName()));
            }
        }
        for (Pair<String,String> left_pair:left){
            for (Pair<String,String> right_pair:right){
                if(left_pair.right.equalsIgnoreCase(right_pair.right)){
                    String left_fullname = left_pair.left + "." + left_pair.right;
                    String right_fullname = right_pair.left + "." + right_pair.right;
                    results.add(new Pair<>(left_fullname,right_fullname));
                    break;
                }
            }
        }
        return results;
    }

    public Row combine_rows(Row left_row,Row right_row){
        ArrayList<Entry> entries = new ArrayList<>();
        entries.addAll(left_row.getEntries());
        entries.addAll(right_row.getEntries());
        return new Row(entries.toArray(new Entry[0]));
    }

    public static Row combine_rows_wo_condition(ArrayList<Row> rows) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Row row :rows){
            entries.addAll(row.getEntries());
        }
        return new Row(entries.toArray(new Entry[0]));
    }

    public ArrayList<Row> handle_join(){
        ArrayList<Row> rows = new ArrayList<>();
        if (this.table_name != null){
            Table table = tables.get(0);
            Iterator<Row> iter = table.iterator();
            while (iter.hasNext()){
                rows.add(iter.next());
            }
        }
        else {
            ArrayList<Row> left_rows = left_join.handle_join();
            ArrayList<Row> right_rows = right_join.handle_join();
            if (join_type == JoinType.NATURAL){
                ArrayList<Pair<String,String>> same_column_names = search_same_columns();
                if (same_column_names.size() == 0 )
                    return rows;
                Comparer left_comparer = new Comparer(same_column_names.get(0).left,false,false);
                Comparer right_comparer = new Comparer(same_column_names.get(0).right,false,false);
                Condition condition = new Condition(null,null,null,left_comparer,right_comparer,"=");

                for (int i = 1;i<same_column_names.size();i++){
                    left_comparer = new Comparer(same_column_names.get(i).left,false,false);
                    right_comparer = new Comparer(same_column_names.get(i).right,false,false);
                    Condition temp_condition = new Condition(null,null,null,left_comparer,right_comparer,"=");
                    condition = new Condition(condition,temp_condition,"and",null,null,null);
                }
                on_condition = condition;
            }
            boolean remain_left = false;
            boolean remain_right = false;
            if (join_type == JoinType.FULL_OUTER){
                remain_left = true;
                remain_right = true;
            }
            if (join_type == JoinType.LEFT_OUTER){
                remain_left = true;
                remain_right = false;
            }
            if (join_type == JoinType.RIGHT_OUTER){
                remain_left = false;
                remain_right = true;
            }
            boolean[] left_matched = new boolean[left_rows.size()];
            boolean[] right_matched = new boolean[right_rows.size()];

            for (int i =0;i<left_rows.size();i++) {
                Row left = left_rows.get(i);
                for (int j = 0;j<left_rows.size();j++) {
                    Row right = right_rows.get(j);
                    Row combined = combine_rows(left, right);
                    ArrayList<Integer> table_sizes = new ArrayList<>();
                    for (Table table:tables){
                        if (table_sizes.isEmpty())
                            table_sizes.add(table.columns.size());
                        else
                            table_sizes.add(table_sizes.get(table_sizes.size()-1) + table.columns.size());
                    }
                    if (on_condition==null || on_condition.evaluate(combined, tables,table_sizes)) {
                        rows.add(combined);
                        left_matched[i] = true;
                        right_matched[j] = true;
                    }
                }
            }
            if (remain_left){
                int right_size = right_rows.get(0).getEntries().size();
                for (int i =0;i<left_rows.size();i++){
                    if (!left_matched[i]){
                        ArrayList<Entry> entries = left_rows.get(i).getEntries();
                        for(int j =0;j<right_size;j++)
                            entries.add(null);
                        rows.add(new Row(entries.toArray(new Entry[0])));
                    }
                }
            }
            if (remain_right){
                int left_size = left_rows.get(0).getEntries().size();
                for (int i =0;i<right_rows.size();i++){
                    if (!right_matched[i]){
                        ArrayList<Entry> entries = right_rows.get(i).getEntries();
                        for(int j =0;j<left_size;j++)
                            entries.add(null);
                        rows.add(new Row(entries.toArray(new Entry[0])));
                    }
                }
            }
        }
        return rows;
    }

}

