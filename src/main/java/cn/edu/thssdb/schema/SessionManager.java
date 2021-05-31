package cn.edu.thssdb.schema;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    public static SessionManager getInstance(){
        return SessionManager.ManagerHolder.INSTANCE;
    }

    private static Map<Long,Session> sessionMap = new HashMap<>();

    private SessionManager(){

    }

    public Session getSessionById(long sessionId){
        return sessionMap.get(sessionId);
    }

    public void createUser(String username,String password){

    }

    public void deleteUser(String username){

    }

    private static class ManagerHolder {
        private static final SessionManager INSTANCE = new SessionManager();
        private ManagerHolder() {
        }
    }

    public void addSession(long sessionId,String currentDatabaseName){
        Session session = new Session(sessionId,currentDatabaseName);
        sessionMap.put(sessionId,session);
    }
}
