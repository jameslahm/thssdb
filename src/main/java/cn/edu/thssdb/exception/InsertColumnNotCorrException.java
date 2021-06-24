package cn.edu.thssdb.exception;

public class InsertColumnNotCorrException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Insert data and columns size not equal";
    }
}
