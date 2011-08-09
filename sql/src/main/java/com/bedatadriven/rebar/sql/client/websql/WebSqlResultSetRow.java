package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gears.client.database.ResultSet;

public final class WebSqlResultSetRow extends JavaScriptObject implements SqlResultSetRow {

  protected WebSqlResultSetRow() {

  }

  public native String getString(String columnName) /*-{
    return this[columnName];
  }-*/;

  public native int getInt(String columnName) /*-{
    return this[columnName];
  }-*/;

  public native double getDouble(String columnName) /*-{
    return this[columnName];
  }-*/;

  public native boolean isNull(String columnName) /*-{
    return this[columnName] == null;
  }-*/;


}
