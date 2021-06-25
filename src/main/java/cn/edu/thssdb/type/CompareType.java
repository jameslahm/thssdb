package cn.edu.thssdb.type;

import cn.edu.thssdb.exception.BadComparerException;

public enum CompareType {
    EQ,NE,LT,GT,LE,GE;

    public static boolean compare(String left_value, String right_value) {
        return left_value.equals(right_value);
    }
    public static boolean compare(Comparable left_value,Comparable right_value,CompareType type){
        switch (type){
            case EQ:
                return left_value.equals(right_value);
            case NE:
                return !left_value.equals(right_value);
            case LT:{
                return getDouble(left_value) < getDouble(right_value);
            }
            case GT:
                return getDouble(left_value) > getDouble(right_value);
            case LE:
                return getDouble(left_value) <= getDouble(right_value);
            case GE:
                return getDouble(left_value)>= getDouble(right_value);
            default:
                throw new BadComparerException();
        }
    }
    public static Double getDouble(Comparable value){
        Double left = null;
        if(value instanceof Integer){
            left = new Double((Integer)value);
        } else if(value instanceof Float){
            left = new Double((Float)value);
        } else if(value instanceof Double) {
            left = (Double) value;
        } else {
            throw new BadComparerException();
        }
        return left;
    }
}
