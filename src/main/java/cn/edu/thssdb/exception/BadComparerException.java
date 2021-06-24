package cn.edu.thssdb.exception;

public class BadComparerException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Bad compare in where condition";
    }
}
