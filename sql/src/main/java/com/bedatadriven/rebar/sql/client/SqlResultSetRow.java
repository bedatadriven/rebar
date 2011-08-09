package com.bedatadriven.rebar.sql.client;


public interface SqlResultSetRow {

  public String getString(String columnName);
  public int getInt(String columnName);
  public double getDouble(String columnName);
  public boolean isNull(String columnName);

}
