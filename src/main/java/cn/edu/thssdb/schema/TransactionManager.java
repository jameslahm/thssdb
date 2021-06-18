package cn.edu.thssdb.schema;

import cn.edu.thssdb.statement.BaseStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionManager {
    private ArrayList<Long> inTransactionSessions;
    private SessionManager sessionManager;
    private Logger logger;
    private Database database;
    private HashMap<Long, LinkedList<ReentrantReadWriteLock.ReadLock>> sessionReadLocks;
    private HashMap<Long,LinkedList<ReentrantReadWriteLock.WriteLock>> sessionWriteLocks;

    public TransactionManager(Database database){
        this.database = database;
        this.logger = database.getLogger();
        this.sessionManager = SessionManager.getInstance();
    }

    public void setDatabase(Database database){
        this.database = database;
        this.logger = database.getLogger();
    }

    public boolean exec(BaseStatement statement,long session_id){
        return true;
    }

    public boolean execReadStatement(BaseStatement statement,long session_id){
        return true;
    }

    public boolean execWriteStatement(BaseStatement statement,long session_id){
        return true;
    }

    public boolean BeginTransaction(BaseStatement statement,long session_id){
        return true;
    }

    public boolean CommitTransaction(BaseStatement statement){
        return true;
    }

    public boolean RollbackTransaction(BaseStatement statement,long session_id){
        return true;
    }
}
