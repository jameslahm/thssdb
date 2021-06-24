package cn.edu.thssdb.exception;

public class BadColumnTypeException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Bad column type";
    }
}
