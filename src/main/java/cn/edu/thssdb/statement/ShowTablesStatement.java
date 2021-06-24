package cn.edu.thssdb.statement;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.query.ColumnInfo;
import cn.edu.thssdb.query.TableInfo;
import cn.edu.thssdb.schema.Column;
import cn.edu.thssdb.schema.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowTablesStatement extends BaseStatement{

    @Override
    public SQLEvalResult exec(){
        HashMap<String, Table> tables = getDatabase().getTables();
        ArrayList<TableInfo> table_infos = new ArrayList<>();
        for(Table table:tables.values()){
            table_infos.add(new TableInfo(table));
        }
        SQLEvalResult result = new SQLEvalResult();
        result.setTablesInfo(table_infos);
        result.setShowMeta(false);
        return result;
    }
}
