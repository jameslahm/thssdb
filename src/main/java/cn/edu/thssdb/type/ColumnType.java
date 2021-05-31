package cn.edu.thssdb.type;

public enum ColumnType {
  INT, LONG, FLOAT, DOUBLE, STRING;

  public static ColumnType fromString(String str){
    if (str.equalsIgnoreCase("int"))
      return INT;
    if (str.equalsIgnoreCase("long"))
      return LONG;
    if (str.equalsIgnoreCase("float"))
      return FLOAT;
    if (str.equalsIgnoreCase("double"))
      return DOUBLE;
    if (str.equalsIgnoreCase("string"))
      return STRING;
    //TODO
    // should throw exception
    return null;
  }

  public static String toString(ColumnType type){
    if (type == INT)  return "INT";
    if (type == LONG) return "LONG";
    if (type == FLOAT) return "FLOAT";
    if (type == DOUBLE) return "DOUBLE";
    if (type == STRING) return "STRING";
    // TODO
    // should throw exception
    return null;
  }

  public static Comparable convertDataType(ColumnType type, String value){
    switch (type){
      case INT:
        return Integer.valueOf(value);
      case LONG:
        return Long.valueOf(value);
      case FLOAT:
        return Float.valueOf(value);
      case DOUBLE:
        return Double.valueOf(value);
      default:
        return value;
    }
  }
}
