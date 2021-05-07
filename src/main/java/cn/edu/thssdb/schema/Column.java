package cn.edu.thssdb.schema;

import cn.edu.thssdb.type.ColumnType;

public class Column implements Comparable<Column> {
  private String name;
  private ColumnType type;
  private boolean primary;
  private boolean notNull;
  private int maxLength;

  public void setName(String name) {
    this.name = name;
  }

  public void setType(ColumnType type) {
    this.type = type;
  }

  public boolean isPrimary() {
    return primary;
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  public void setNotNull(boolean notNull) {
    this.notNull = notNull;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public Column(String name, ColumnType type, boolean primary, boolean notNull, int maxLength) {
    this.name = name;
    this.type = type;
    this.primary = primary;
    this.notNull = notNull;
    this.maxLength = maxLength;
  }

  public String getName() {
    return name;
  }

  public ColumnType getType() {
    return type;
  }

  public boolean getPrimary() {
    return primary;
  }

  public boolean isNotNull() {
    return notNull;
  }

  public int getMaxLength() {
    return maxLength;
  }

  @Override
  public int compareTo(Column e) {
    return name.compareTo(e.name);
  }

  public String toString() {
    return name + ',' + type + ',' + primary + ',' + notNull + ',' + maxLength;
  }
}
