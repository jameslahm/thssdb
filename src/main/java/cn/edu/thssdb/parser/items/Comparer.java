package cn.edu.thssdb.parser.items;

import java.io.Serializable;

public class Comparer implements Serializable {
    public String value;
    public boolean is_literal;
    public boolean is_null;
    public Comparer(String value,boolean is_literal,boolean is_null){
        this.value = value;
        this.is_literal = is_literal;
        this.is_null = is_null;
    }
}
