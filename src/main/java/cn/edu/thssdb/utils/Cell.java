package cn.edu.thssdb.utils;


import cn.edu.thssdb.type.AlignType;

public class Cell {
    private AlignType align;
    private String value;

    public Cell(String value) {
        this.align = AlignType.LEFT;
        this.value = value;
    }

    AlignType getAlign() {
        return align;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s", this.value);
    }
}