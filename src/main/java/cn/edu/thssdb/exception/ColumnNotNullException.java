package cn.edu.thssdb.exception;

public class ColumnNotNullException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Not null constraints failed";
    }
}
