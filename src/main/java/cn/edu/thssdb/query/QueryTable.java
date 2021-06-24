package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.schema.Table;
import cn.edu.thssdb.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class QueryTable implements Iterator<Row> {
  private ArrayList<Table> tables;
  private ArrayList<Pair<String,String>> query_columns;
  public QueryTable(ArrayList<Table> tables) {
    this.tables = tables;
  }

  public void setQuery_columns(ArrayList<Pair<String, String>> query_columns) {
    this.query_columns = query_columns;
  }

  public ArrayList<Pair<String, String>> getQuery_columns() {
    return query_columns;
  }

  public void setTables(ArrayList<Table> tables) {
    this.tables = tables;
  }

  public ArrayList<Table> getTables(){
    return tables;
  }
  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public Row next() {
    return null;
  }
}