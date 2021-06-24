package cn.edu.thssdb.schema;

import cn.edu.thssdb.statement.*;
import cn.edu.thssdb.utils.Persist;

import java.io.*;
import java.util.ArrayList;

public class Logger {

    private String path;
    public ArrayList<BaseStatement> redoList = new ArrayList<>();
    public ArrayList<BaseStatement> undoList = new ArrayList<>();

    public Logger(String path){
        this.path = path;
    }

    public ArrayList<BaseStatement> getRedoList() {
        return redoList;
    }

    public ArrayList<BaseStatement> getUndoList() {
        return undoList;
    }

    public void logStatement(BaseStatement statement){
        if (statement instanceof DeleteStatement ||
                statement instanceof UpdateStatement ||
                statement instanceof InsertStatement || statement instanceof CommitStatement ||
                statement instanceof BeginStatement || statement instanceof CreateTableStatement){
            redoList.add(statement);
        }
    }
    public void removeStatement(BaseStatement statement){
        redoList.remove(statement);
    }
    public void writeLog(){
        Persist.serializeLog(path,redoList.iterator());
    }

    public void readLog(){
        this.redoList = Persist.deserialize(path);
    }
};
