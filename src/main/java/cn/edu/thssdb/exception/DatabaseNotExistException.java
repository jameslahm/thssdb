package cn.edu.thssdb.exception;

public class DatabaseNotExistException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Database not exist";
    }
}
