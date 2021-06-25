package cn.edu.thssdb.exception;

public class BadSqlException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Bad Sql";
    }
}
