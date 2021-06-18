package cn.edu.thssdb.schema;

import cn.edu.thssdb.statement.BaseStatement;

import java.io.*;
import java.util.ArrayList;

public class Logger {
    private String path;
    private ArrayList<BaseStatement> redo_list;
    private ArrayList<BaseStatement[]> undo_list;

    public Logger(String path){
        this.path = path;
    }

    public void log_statement(BaseStatement statement){
        return;
    }

    public void write_log(){
        return;
    }

    public void read_log(){
        return;
    }
};
