package com.bedatadriven.rebar.dao.model;

public class ColumnReference {
  private String table;
  private String column;

  public ColumnReference(String reference) {
    int dot = reference.indexOf('.');
    if (dot == -1) {
      column = reference;
    } else {
      table = reference.substring(0, dot);
      column = reference.substring(dot + 1);
    }
  }

  public boolean isTableSpecified() {
    return table != null;
  }

  public String getTable() {
    return table;
  }

  public String getColumn() {
    return column;
  }

}
