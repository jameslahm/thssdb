package cn.edu.thssdb.exception;

public class PrimaryKeyEmptyException extends RuntimeException{

    @Override
    public String getMessage() {
        return "Primary ket can't be empty";
    }
}
