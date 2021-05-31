package cn.edu.thssdb.schema;

public class Session {
    private long sessionId;
    private String currentDatabaseName;

    public Session(long sessionId, String databaseName){
        this.sessionId = sessionId;
        this.currentDatabaseName = databaseName;
    }

    public void UseDatabase(String databaseName){
        this.currentDatabaseName = databaseName;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }
}
