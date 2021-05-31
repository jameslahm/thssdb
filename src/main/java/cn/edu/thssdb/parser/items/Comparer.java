package cn.edu.thssdb.parser.items;

public class Comparer {
    public String value;
    public boolean is_literal;
    public boolean is_null;
    public Comparer(String value,boolean is_literal,boolean is_null){
        this.value = value;
        this.is_literal = is_literal;
        this.is_null = is_null;
    }
}
