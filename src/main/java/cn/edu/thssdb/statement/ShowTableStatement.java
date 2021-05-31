package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.query.TableInfo;
import cn.edu.thssdb.schema.Table;
import javafx.scene.control.Tab;

import java.util.ArrayList;

public class ShowTableStatement extends BaseStatement{
    private String table_name;

    public ShowTableStatement(String table_name){
        this.table_name = table_name;
    }

    @Override
    public SQLEvalResult exec(){
        Table table = database.getTableByName(table_name);
        SQLEvalResult result = new SQLEvalResult();
        ArrayList<TableInfo> tableInfos = new ArrayList<>();
        tableInfos.add(new TableInfo(table));
        result.setTablesInfo(tableInfos);
        result.setShowMeta(true);
        return result;
    }
}
