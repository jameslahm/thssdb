package cn.edu.thssdb.type;

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
            case LT:
                return (Double) left_value < (Double) right_value;
            case GT:
                return (Double)left_value > (Double) right_value;
            case LE:
                return (Double)left_value <= (Double) right_value;
            default:
                return (Double)left_value >= (Double) right_value;
        }
    }
}
