package cn.edu.thssdb.utils;

public class Global {
  public enum ISOLATION_LEVEL {
    READ_UNCOMMITTED,READ_COMMITTED,SERIALIZABLE;
  }
  public static ISOLATION_LEVEL DATABASE_ISOLATION_LEVEL = ISOLATION_LEVEL.READ_UNCOMMITTED;
  public static int fanout = 129;

  public static int SESSION_ID = 1;

  public static int SUCCESS_CODE = 0;
  public static int FAILURE_CODE = -1;

  public static String DEFAULT_SERVER_HOST = "127.0.0.1";
  public static int DEFAULT_SERVER_PORT = 6667;
  public static int DEFAULT_SERVERR_PORT2 = 6668;

  public static String CLI_PREFIX = "ThssDB>";
  public static final String SHOW_TIME = "show time;";
  public static final String QUIT = "quit;";
  public static final String CONNECT = "connect;";
  public static final String DISCONNECT = "disconnect;";
  public static final String USERNAME = "aaa";
  public static final String PASSWORD = "abc";

  public static final String S_URL_INTERNAL = "jdbc:default:connection";

  public static final String DATA_FOLDER = "data";


}
