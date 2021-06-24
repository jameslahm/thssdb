package cn.edu.thssdb.schema;

import cn.edu.thssdb.parser.SQLEvalResult;
import cn.edu.thssdb.parser.SQLParser;
import cn.edu.thssdb.statement.*;
import cn.edu.thssdb.utils.Global;
import cn.edu.thssdb.utils.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SavePoint{
    int position;
    String name;

    public SavePoint(int position,String name){
        this.position = position;
        this.name = name;
    }

}

public class TransactionManager {
    private HashMap<Long,ArrayList<BaseStatement>> inTransactionSessions;
    private SessionManager sessionManager;
    private Logger logger;
    private Database database;
    private HashMap<Long, LinkedList<ReentrantReadWriteLock.ReadLock>> sessionReadLocks;
    private HashMap<Long,LinkedList<ReentrantReadWriteLock.WriteLock>> sessionWriteLocks;
    private HashMap<Long,ArrayList<SavePoint>> sessionSavepoints;

    public TransactionManager(Database database){
        this.database = database;
        this.logger = database.getLogger();
        this.sessionManager = SessionManager.getInstance();
        this.sessionReadLocks = new HashMap<>();
        this.sessionWriteLocks = new HashMap<>();
        this.inTransactionSessions = new HashMap<>();
        this.sessionSavepoints = new HashMap<>();
    }

    public void setDatabase(Database database){
        this.database = database;
        this.logger = database.getLogger();
    }

    public Pair<SQLEvalResult,Boolean> exec(BaseStatement statement){
        long session_id = statement.session_id;
        Pair<SQLEvalResult,Boolean> result;
        boolean isInTransaction = inTransactionSessions.containsKey(session_id);
        if (statement instanceof SelectStatement ||
                statement instanceof ShowDatabaseStatement ||
                statement instanceof ShowTablesStatement ||
                statement instanceof ShowTableStatement)
        {
            result = execReadStatement(statement,session_id);
            if(!isInTransaction){
                CommitStatement commitStatement = new CommitStatement();
                commitStatement.setSession(SessionManager.getInstance().getSessionById(session_id));
                CommitTransaction(commitStatement, session_id);
            }
        }
        else if (statement instanceof UpdateStatement || statement instanceof DeleteStatement
                || statement instanceof  InsertStatement || statement instanceof DropDatabaseStatement
                || statement instanceof DropTableStatement){
            result = execWriteStatement(statement,session_id);
            if(!isInTransaction){
                CommitStatement commitStatement = new CommitStatement();
                commitStatement.setSession(SessionManager.getInstance().getSessionById(session_id));
                CommitTransaction(commitStatement, session_id);
            }
        }
        else if (statement instanceof CommitStatement){
            result = CommitTransaction(statement,session_id);
        }
        else if (statement instanceof RollbackStatement){
            result = RollbackTransaction(statement,session_id);
        }
        else if (statement instanceof SavepointStatement){
            result = SavepointTransaction(statement,session_id);
        }
        else if (statement instanceof BeginStatement){
            result = BeginTransaction(statement,session_id);
        }
        else if (statement instanceof CheckpointStatement){
            result = CheckpointTransaction(statement,session_id);
        }
        else if(statement instanceof CreateTableStatement){
            SQLEvalResult res = statement.exec();
            logger.logStatement(statement);
            logger.writeLog();
            result = new Pair<>(res,true);
        }
        else{
            result = MetaTransaction(statement,session_id);
        }

        if (result.right != false){
            if(inTransactionSessions.containsKey(session_id)){
                inTransactionSessions.get(session_id).add(statement);
            }
            statement.session_id = session_id;
        }
        return result;
    }

    public Pair<SQLEvalResult,Boolean> execReadStatement(BaseStatement statement, long session_id){
        SQLEvalResult result = new SQLEvalResult();
        // necessary because not all transaction start with begin transaction but also single statement
        if (!this.inTransactionSessions.containsKey(session_id)){
            this.inTransactionSessions.put(session_id,new ArrayList<>());
        }
        if (!this.sessionSavepoints.containsKey(session_id)){
            this.sessionSavepoints.put(session_id,new ArrayList<>());
        }
        if (!this.sessionReadLocks.containsKey(session_id)){
            this.sessionReadLocks.put(session_id,new LinkedList<>());
        }
        if (!this.sessionWriteLocks.containsKey(session_id)){
            this.sessionWriteLocks.put(session_id,new LinkedList<>());
        }
        if (Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.READ_UNCOMMITTED ||Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.READ_COMMITTED){
            ArrayList<String> tableNames = statement.getTableNames();
            for (String tableName:tableNames){
                getTableReadLock(tableName,session_id);
            }
            try{
                result = statement.exec();
            } catch(Exception e){
                for (String tableName:tableNames){
                    releaseTableReadLock(tableName,session_id);
                }
                result.error = e;
                return new Pair<>(result,false);
            }
            for (String tableName:tableNames){
                releaseTableReadLock(tableName,session_id);
            }
        }
        else if (Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.SERIALIZABLE){
            ArrayList<String> tableNames = statement.getTableNames();
            for (String tableName:tableNames){
                getTableWriteLock(tableName,session_id);
            }
            try {
                result = statement.exec();
            } catch (Exception e){
//                for (String tableName:tableNames){
//                    releaseTableReadLock(tableName);
//                }
                result.error = e;
                return new Pair<>(result,false);
            }
        }
        if(result.error==null){
            logger.logStatement(statement);
            logger.writeLog();
        }
        return new Pair<>(result,true);
    }

    public Pair<SQLEvalResult,Boolean> execWriteStatement(BaseStatement statement,long session_id){
        // necessary because not all transaction start with begin transaction but also single statement
        if (!this.inTransactionSessions.containsKey(session_id)){
            this.inTransactionSessions.put(session_id,new ArrayList<>());
        }
        if (!this.sessionSavepoints.containsKey(session_id)){
            this.sessionSavepoints.put(session_id,new ArrayList<>());
        }
        if (!this.sessionReadLocks.containsKey(session_id)){
            this.sessionReadLocks.put(session_id,new LinkedList<>());
        }
        if (!this.sessionWriteLocks.containsKey(session_id)){
            this.sessionWriteLocks.put(session_id,new LinkedList<>());
        }

        SQLEvalResult result = new SQLEvalResult();
        ArrayList<String> tableNames = statement.getTableNames();
        for(String tableName:tableNames) {
            getTableWriteLock(tableName,session_id);
        }
        if (Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.READ_UNCOMMITTED){
            try{
                result = statement.exec();
            } catch(Exception e){
                for (String tableName:tableNames){
                    releaseTableWriteLock(tableName,session_id);
                }
                result.error = e;
                return new Pair<>(result,false);
            }
            for (String tableName:tableNames){
                releaseTableWriteLock(tableName,session_id);
            }
        }
        else if (Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.READ_COMMITTED ||
            Global.DATABASE_ISOLATION_LEVEL == Global.ISOLATION_LEVEL.SERIALIZABLE){
            try{
                result = statement.exec();
            } catch(Exception e){
                result.error = e;
                return new Pair<>(result,false);
            }
        }
        if(result.error==null){
            logger.logStatement(statement);
            logger.writeLog();
        }
        return new Pair<>(result,true);
    }

    public Pair<SQLEvalResult,Boolean> BeginTransaction(BaseStatement statement,long session_id){
        if (!this.inTransactionSessions.containsKey(session_id)){
            this.inTransactionSessions.put(session_id,new ArrayList<>());
        }
        if (!this.sessionSavepoints.containsKey(session_id)){
            this.sessionSavepoints.put(session_id,new ArrayList<>());
        }
        if (!this.sessionReadLocks.containsKey(session_id)){
            this.sessionReadLocks.put(session_id,new LinkedList<>());
        }
        if (!this.sessionWriteLocks.containsKey(session_id)){
            this.sessionWriteLocks.put(session_id,new LinkedList<>());
        }
        logger.logStatement(statement);
        logger.writeLog();
        return new Pair<>(new SQLEvalResult(),true);
    }

    public Pair<SQLEvalResult,Boolean> CommitTransaction(BaseStatement statement,long session_id){
        logger.logStatement(statement);
        logger.writeLog();
        this.releaseTransactionLocks(session_id);
        this.sessionReadLocks.remove(session_id);
        this.sessionWriteLocks.remove(session_id);
        this.inTransactionSessions.remove(session_id);
        this.sessionSavepoints.remove(session_id);
        return new Pair<>(new SQLEvalResult(),true);
    }

    public Pair<SQLEvalResult,Boolean> RollbackTransaction(BaseStatement statement,long session_id){
        String savepoint_name = ((RollbackStatement) statement).getSavepoint_name();
        ArrayList<SavePoint> savePoints = this.sessionSavepoints.get(session_id);
        int rollback_pos = 0;

        if (savepoint_name ==null){
            rollback_pos = 0;
        }
        else {
            int savepoint_pos = -1;
            for (int i =0;i<savePoints.size();i++){
                if (savePoints.get(i).name.equals(savepoint_name)){
                    savepoint_pos = i;
                    rollback_pos = savePoints.get(i).position;
                    break;
                }
            }
            if (savepoint_pos != -1){
                while (savePoints.size() > savepoint_pos){
                    savePoints.remove(savePoints.size()-1);
                }
            }
            else {
                return new Pair<>(new SQLEvalResult(),true);
            }
        }
        ArrayList<BaseStatement> TransactionStatements = inTransactionSessions.get(session_id);
        while (TransactionStatements.size() > rollback_pos){
            BaseStatement stat = TransactionStatements.remove(TransactionStatements.size()-1);
            logger.removeStatement(stat);
            stat.undo();
        }
        logger.writeLog();
        return new Pair<>(new SQLEvalResult(),true);
    }

    public Pair<SQLEvalResult,Boolean> SavepointTransaction(BaseStatement statement,long session_id){
        if (this.sessionSavepoints.containsKey(session_id)){
            ArrayList<SavePoint> savepoints = this.sessionSavepoints.get(session_id);
            savepoints.add(new SavePoint(this.inTransactionSessions.get(session_id).size(),((SavepointStatement)statement).getSavepoint_name()));
            return new Pair<>(new SQLEvalResult(),true);
        }
        return new Pair<>(new SQLEvalResult(),true);
    }

    public Pair<SQLEvalResult,Boolean> CheckpointTransaction(BaseStatement statement,long session_id) {
//        for (long session : inTransactionSessions.keySet()){
//            CommitTransaction(new BaseStatement(),session);
//            BeginTransaction(new BaseStatement(),session);
//        }
        database.persist();
        return new Pair<>(new SQLEvalResult(),true);
    }

    public Pair<SQLEvalResult,Boolean> MetaTransaction(BaseStatement statement,long session_id){
        SQLEvalResult result = new SQLEvalResult();
        try{
            result = statement.exec();
        } catch (Exception e){
            result.error = e;
            return new Pair<>(result,true);
        }
        return new Pair<>(result,true);
    }

    private void releaseTransactionLocks(Long session_id){
        LinkedList<ReentrantReadWriteLock.ReadLock> readLocks = sessionReadLocks.get(session_id);
        LinkedList<ReentrantReadWriteLock.WriteLock> writeLocks = sessionWriteLocks.get(session_id);
        while (!readLocks.isEmpty()){
            readLocks.remove().unlock();
        }
        while (!writeLocks.isEmpty()) {
            writeLocks.remove().unlock();
        }
    }

    private void getTableReadLock(String tableName,long session_id){
        Table table = database.getTableByName(tableName);
        ReentrantReadWriteLock.ReadLock readLock = table.lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = table.lock.writeLock();
        if (! (sessionReadLocks.get(session_id).contains(readLock) || sessionWriteLocks.get(session_id).contains(writeLock))){
            readLock.lock();
            sessionReadLocks.get(session_id).add(readLock);
        }
    }
    private void getTableWriteLock(String tableName,long session_id){
        Table table = database.getTableByName(tableName);
        ReentrantReadWriteLock.WriteLock writeLock = table.lock.writeLock();
        if (! sessionWriteLocks.get(session_id).contains(writeLock)){
            writeLock.lock();
            sessionWriteLocks.get(session_id).add(writeLock);
        }
    }
    private void releaseTableReadLock(String tableName,long session_id){
        Table table = database.getTableByName(tableName);
        ReentrantReadWriteLock.ReadLock readLock = table.lock.readLock();
        LinkedList<ReentrantReadWriteLock.ReadLock> readLockList = sessionReadLocks.get(session_id);
        if (readLockList.remove(readLock)){
            readLock.unlock();
        }
    }
    private void releaseTableWriteLock(String tableName,long session_id){
        Table table = database.getTableByName(tableName);
        ReentrantReadWriteLock.WriteLock writeLock = table.lock.writeLock();
        LinkedList<ReentrantReadWriteLock.WriteLock> writeLockList = sessionWriteLocks.get(session_id);
        if (writeLockList.remove(writeLock)){
            writeLock.unlock();
        }
    }

}

