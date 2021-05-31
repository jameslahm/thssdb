package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.utils.Cell;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class QueryResult {

  private List<Row> rows;
  private List<Cell> attrs;

  public QueryResult(List<Row> rows,List<Cell> attrs) {
    // TODO
    this.rows = rows;
    this.attrs = attrs;
  }

  public List<String> columnsToString(){
    List<String> colStrings =  this.attrs.stream().map((attr)->{
      return attr.toString();
    }).collect(Collectors.toList());
    return colStrings;
  }

  public List<List<String>> rowsToString(){
    List<List<String>> rowsString = this.rows.stream().map((row)->{
      return row.getEntries().stream().map((entry -> {
        return entry.toString();
      })).collect(Collectors.toList());
    }).collect(Collectors.toList());
    return rowsString;
  }
}
