package cn.edu.thssdb.parser.items;

import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Entry;
import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.statement.SelectStatement;
import cn.edu.thssdb.type.ColumnType;
import cn.edu.thssdb.type.CompareType;
import cn.edu.thssdb.type.LogicType;
import cn.edu.thssdb.utils.Pair;

import java.util.ArrayList;

public class Condition {
    Condition left_cond;
    Condition right_cond;
    LogicType logic_op;
    Comparer left_comparer;
    Comparer right_comparer;
    CompareType comparator;

    public Condition(Condition left_cond,Condition right_cond,String logic_op, Comparer left_comparer,Comparer right_comparer,String comparator){
        this.left_cond = left_cond;
        this.right_cond = right_cond;
        this.left_comparer = left_comparer;
        this.right_comparer = right_comparer;

        if (logic_op == null){
            this.logic_op = null;
        }
        else if (logic_op.equalsIgnoreCase("and")){
            this.logic_op = LogicType.AND;
        }
        else if(logic_op.equalsIgnoreCase("or")){
            this.logic_op = LogicType.OR;
        }

        if (comparator.equalsIgnoreCase("=")) this.comparator = CompareType.EQ;
        else if (comparator.equalsIgnoreCase("<>")) this.comparator = CompareType.NE;
        else if (comparator.equalsIgnoreCase("<")) this.comparator = CompareType.LT;
        else if (comparator.equalsIgnoreCase(">")) this.comparator = CompareType.GT;
        else if (comparator.equalsIgnoreCase("<=")) this.comparator = CompareType.LE;
        else if (comparator.equalsIgnoreCase(">=")) this.comparator = CompareType.GE;
    }

    public boolean evaluate(Row row, Table table) {
        if (this.left_cond != null){
            if (logic_op == LogicType.AND){
                return left_cond.evaluate(row,table) && right_cond.evaluate(row,table);
            }
            else if (logic_op == LogicType.OR){
                return left_cond.evaluate(row,table) || right_cond.evaluate(row,table);
            }
            return true;
        }
        else if (left_comparer.is_literal) {
            String left_value = left_comparer.value;
            if (right_comparer.is_literal) {
                String right_value = right_comparer.value;
                if (left_comparer.is_null || right_comparer.is_null){
                    if (left_comparer.is_null && right_comparer.is_null)
                        return true;
                    else
                        return false;
                }
                if (comparator == CompareType.EQ){
                    return CompareType.compare(left_value,right_value,comparator);
                }
                else{
                    return CompareType.compare(ColumnType.convertDataType(ColumnType.DOUBLE,left_value),
                            ColumnType.convertDataType(ColumnType.DOUBLE,right_value),comparator);
                }
            } else {
                String[] pair = right_comparer.value.split("[.]");
                String column_name;
                if (pair.length==1){
                    column_name = pair[0];
                }
                else {
                    column_name = pair[1];
                }
                int index = table.columnToIndex(column_name);
                ColumnType type = table.columns.get(index).getType();
                Entry x = row.getEntries().get(index);
                // TODO
                //handle null
                return CompareType.compare(ColumnType.convertDataType(type,left_value),x.value,comparator);
            }
        }
        else {
            String[] left_pair = left_comparer.value.split("[.]");
            String left_column_name;
            if (left_pair.length==1){
                left_column_name = left_pair[0];
            }
            else {
                left_column_name = left_pair[1];
            }
            int left_index = table.columnToIndex(left_column_name);
            ColumnType left_type = table.columns.get(left_index).getType();
            Entry left_entry = row.getEntries().get(left_index);

            if (right_comparer.is_literal) {
                String right_value = right_comparer.value;
                return CompareType.compare(left_entry.value,ColumnType.convertDataType(left_type,right_value),comparator);
            } else {
                String[] right_pair = right_comparer.value.split("[.]");
                String right_column_name;
                if (right_pair.length==1){
                    right_column_name = right_pair[0];
                }
                else {
                    right_column_name = right_pair[1];
                }
                int right_index =table.columnToIndex(right_column_name);
                Entry right_entry = row.getEntries().get(right_index);
                return CompareType.compare(left_entry.value,right_entry.value,comparator);
            }
        }

    }

    public boolean evaluate(Row row, ArrayList<Table> tables,ArrayList<Integer> table_locs){
        if(left_cond!=null){
            if(logic_op == LogicType.AND){
                return left_cond.evaluate(row,tables,table_locs) && right_cond.evaluate(row,tables,table_locs);
            }
            else{
                return left_cond.evaluate(row,tables,table_locs) || right_cond.evaluate(row,tables,table_locs);
            }
        }
        String left = left_comparer.value;
        String right = right_comparer.value;
        if (left_comparer.is_literal){
            if (right_comparer.is_literal){
                if (left_comparer.is_null || right_comparer.is_null){
                    if (left_comparer.is_null && right_comparer.is_null)
                        return true;
                    else
                        return false;
                }
                return CompareType.compare(left,right,comparator);
            }
            else {
                Pair<Integer,Integer> pair = SelectStatement.getIndexFromTables(tables,right);
                Table table = tables.get(pair.left);
                Entry right_entry = row.getEntries().get(table_locs.get(pair.left) + pair.right);
                Column right_column = table.columns.get(pair.right);
                if (left_comparer.is_null && right_column.isNotNull()){
                    //TODO
                    throw new RuntimeException();
                }
                Comparable left_value = ColumnType.convertDataType(right_column.getType(),left);
                return CompareType.compare(left_value,right_entry.value,comparator);
            }
        }
        else{
            Pair<Integer,Integer> left_pair = SelectStatement.getIndexFromTables(tables,left);
            Table left_table =tables.get(left_pair.left);
            Entry left_entry = row.getEntries().get(table_locs.get(left_pair.left)+left_pair.right);
            Column left_column = left_table.columns.get(left_pair.right);
            if (right_comparer.is_literal){
                if (right_comparer.is_null && left_column.isNotNull()){
                    //TODO
                    throw new RuntimeException();
                }
                Comparable right_value = ColumnType.convertDataType(left_column.getType(),right);
                return CompareType.compare(left_entry.value,right_value,comparator);
            }
            else{
                Pair<Integer,Integer> right_pair = SelectStatement.getIndexFromTables(tables,right);
                Entry right_entry = row.getEntries().get(table_locs.get(right_pair.left) + right_pair.right);
                return CompareType.compare(left_entry.value,right_entry.value,comparator);
            }
        }
    }
}
