package cn.edu.thssdb.schema;


import cn.edu.thssdb.type.ColumnType;
import javafx.scene.control.Tab;
import java.util.ArrayList;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

public class TableTest {
    private Table table;
    private String databaseName;
    private String tableName;

    private Table newTable;

    private ArrayList<Column> columns;

    @BeforeEach
    public void setUp(){
        databaseName = "course";
        tableName = "person";
        generateColumns();
        table = new Table(databaseName,tableName,columns.toArray(new Column[]{}));
        newTable = new Table(databaseName,tableName,columns.toArray(new Column[]{}));
    }

    public void generateColumns(){
        columns = new ArrayList<>();
        Column id = new Column("id", ColumnType.INT,true,true,0);
        Column name = new Column("name",ColumnType.STRING,false,true,10);
        columns.add(id);
        columns.add(name);
    }

    public Row generateRow(int id,String name){
        Entry idEntry = new Entry(id);
        Entry nameEntry = new Entry(name);
        Entry[] entries = new Entry[]{idEntry,nameEntry};
        return new Row(entries);
    }

    @Test
    public void testInsert(){
        Row row = generateRow(0,"alice");
        table.insert(row);
        Entry entry =  row.getEntries().get(0);
        Row res = table.get(entry);
        Assertions.assertEquals(res,row);

        table.delete(row);
    }

    @Test
    public void testDelete(){
        Row row = generateRow(0,"alice");
        table.insert(row);

        table.delete(row);
        Entry entry =  row.getEntries().get(0);
        Row res = table.get(entry);
        Assertions.assertEquals(res,null);
    }

    @Test
    public void testUpdateUnSamePrimaryKey(){
        Row oldRow = generateRow(0,"alice");
        table.insert(oldRow);

        Row newRow = generateRow(1,"bob");

        table.update(newRow,oldRow);

        Row res= table.get(newRow.getEntries().get(0));
        Assertions.assertEquals(res,newRow);

        res = table.get(oldRow.getEntries().get(0));
        Assertions.assertEquals(res,null);

        table.delete(newRow);
    }


    @Test
    public void testUpdateSamePrimaryKey(){
        Row oldRow = generateRow(0,"alice");
        table.insert(oldRow);

        Row newRow = generateRow(0,"bob");

        table.update(newRow,oldRow);

        Row res= table.get(newRow.getEntries().get(0));
        Assertions.assertEquals(res,newRow);

        table.delete(newRow);
    }

    @Test
    public void testSerializeAndDeserialize(){
        Row row0 = generateRow(0,"alice");
        Entry entry0 = row0.getEntries().get(0);
        Row row1 = generateRow(1,"bob");
        Entry entry1 = row1.getEntries().get(0);

        table.insert(row0);
        table.insert(row1);

        table.persist();

        newTable.recover();
        Row row2 = newTable.get(entry0);
        Assertions.assertEquals(row2.getEntries().get(0),entry0);
        Assertions.assertEquals(row2.getEntries().get(1).value,"alice");

        Row row3 = newTable.get(entry1);
        Assertions.assertEquals(row3.getEntries().get(0),entry1);
        Assertions.assertEquals(row3.getEntries().get(1).value,"bob");
    }
}
