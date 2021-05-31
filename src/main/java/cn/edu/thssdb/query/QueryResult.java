package cn.edu.thssdb.query;

import cn.edu.thssdb.schema.Row;
import cn.edu.thssdb.utils.Cell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueryResult {

  private List<Row> rows;
  private List<Cell> attrs;

  public QueryResult(List<Row> rows,List<Cell> attrs) {
    // TODO
    this.rows = rows;
    this.attrs = attrs;
  }

}
