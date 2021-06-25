package cn.edu.thssdb.schema;

import cn.edu.thssdb.exception.KeyNotExistException;
import cn.edu.thssdb.index.BPlusTree;
import cn.edu.thssdb.type.ColumnType;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Pair;
import cn.edu.thssdb.utils.Persist;
import org.w3c.dom.traversal.DocumentTraversal;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Table implements Iterable<Row> {
  ReentrantReadWriteLock lock;
  private String databaseName;
  public String tableName;
  public ArrayList<Column> columns;
  public BPlusTree<Entry, Row> index;
  private int primaryIndex;
  public Table(String databaseName, String tableName, Column[] columns) {
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.columns = new ArrayList<>(Arrays.asList(columns));
    this.index = new BPlusTree<>();
    initPrimaryIndex();
    this.lock = new ReentrantReadWriteLock();
  }


  public void initPrimaryIndex(){
    for (int i=0;i<columns.size();i++){
      Column column = this.columns.get(i);
      if(column.getPrimary()){
        primaryIndex = i;
        break;
      }
    }
  }

  public Table(String databaseName,String tableName){
    this.databaseName = databaseName;
    this.tableName = tableName;
    this.index = new BPlusTree<>();
    this.lock = new ReentrantReadWriteLock();
    recoverMeta();
    recover();
  }

  public void addColumn(Column column){
    this.columns.add(column);
    TableIterator iterator = new TableIterator(this);
    while(iterator.hasNext()){
      Row row = iterator.next();
      row.getEntries().add(new Entry(null));
    }
  }

  public void dropColumn(String columnName){
    for(int i=0;i<columns.size();i++){
      if(columns.get(i).getName().equals(columnName)){
        columns.remove(i);
        TableIterator iterator = new TableIterator(this);
        while(iterator.hasNext()){
          Row row = iterator.next();
          row.getEntries().remove(i);
        }
        break;
      }
    }
  }

  private String getMetaPath(){
    return Paths.get(Global.DATA_FOLDER,databaseName,tableName+".meta").toString();
  }

  public void recoverMeta(){
    ArrayList<Column> columns = Persist.fromJsonToTableMeta(getMetaPath());
    this.columns = columns;
    initPrimaryIndex();
  }

  public void recover() {
    ArrayList<Row> rows = deserialize();
    for (Row row:rows){
      index.put(getPrimaryEntry(row),row);
    }
  }

  public Row get(Entry entry){
    try {
      return index.get(entry);
    } catch (KeyNotExistException exception){
      return null;
    }
  }

  public void persist(){
    Persist.fromTableMetaToJson(columns,getMetaPath());
    serialize();
  }

  public void insert(Row row) {
    Entry entry = getPrimaryEntry(row);
    index.put(entry,row);
  }

  public void delete(Row row) {
    Entry entry = getPrimaryEntry(row);
    index.remove(entry);
  }

  public void update(Row newRow, Row oldRow) {
    Entry oldEntry = getPrimaryEntry(oldRow);
    Entry newEntry = getPrimaryEntry(newRow);
    if(oldEntry.equals(newEntry)){
      index.update(newEntry,newRow);
    } else {
      index.remove(oldEntry);
      index.put(newEntry,newRow);
    }
  }

  public void drop(){
    Persist.deleteFile(getDataFilePath());
    Persist.deleteFile(getMetaPath());
    index = null;
    columns = null;
    primaryIndex = -1;
    tableName = null;
    databaseName = null;
  }

  public Entry getPrimaryEntry(Row row){
    return row.getEntries().get(primaryIndex);
  }

  public String getDataFilePath(){
    String path = Paths.get(Global.DATA_FOLDER,databaseName,tableName+".data").toString();
    return path;
  }

  public int columnToIndex(String column_name){
    for (int i = 0;i<columns.size();i++){
      if (columns.get(i).getName().equalsIgnoreCase(column_name)){
        return i;
      }
    }
    return -1;
  }

  private void serialize() {
    Iterator iterator = new TableIterator(this);
    Persist.serialize(getDataFilePath(),iterator);
  }

  private ArrayList<Row> deserialize() {
    return Persist.deserialize(getDataFilePath());
  }

  private class TableIterator implements Iterator<Row> {
    private Iterator<Pair<Entry, Row>> iterator;

    TableIterator(Table table) {
      this.iterator = table.index.iterator();
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Row next() {
      return iterator.next().right;
    }
  }

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(this);
  }
}
