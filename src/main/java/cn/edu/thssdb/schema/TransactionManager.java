package cn.edu.thssdb.schema;

import cn.edu.thssdb.statement.*;

import java.sql.Savepoint;
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
        this.sessionReadLocks = new HashMap<>();
        this.sessionWriteLocks = new HashMap<>();
        this.inTransactionSessions = new ArrayList<>();
    }

    public void setDatabase(Database database){
        this.database = database;
        this.logger = database.getLogger();
    }

    public boolean exec(BaseStatement statement,long session_id){
        if (statement instanceof SelectStatement ||
            statement instanceof ShowDatabaseStatement ||
            statement instanceof ShowTablesStatement ||
            statement instanceof ShowTableStatement)
        {
            return execReadStatement(statement,session_id);
        }
        else if (statement instanceof UpdateStatement || statement instanceof DeleteStatement
        || statement instanceof  InsertStatement){
            return execWriteStatement(statement,session_id);
        }
        else if (statement instanceof CommitStatement){
            return CommitTransaction(statement,session_id);
        }
        else if (statement instanceof RollbackStatement){
            return RollbackTransaction(statement,session_id);
        }
        else if (statement instanceof SavepointStatement){
            return SavepointTransaction(statement,session_id);
        }
        else if (statement instanceof BeginStatement){
            return BeginTransaction(statement,session_id);
        }
        return false;
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

    public boolean CommitTransaction(BaseStatement statement,long session_id){
        return true;
    }

    public boolean RollbackTransaction(BaseStatement statement,long session_id){
        return true;
    }

    public boolean SavepointTransaction(BaseStatement statement,long session_id){
        return true;
    }
}
