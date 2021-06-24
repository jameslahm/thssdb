package cn.edu.thssdb.exception;

public class TableNotExistException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Table not exist";
    }
}
