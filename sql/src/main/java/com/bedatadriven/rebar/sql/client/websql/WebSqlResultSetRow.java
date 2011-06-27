package com.bedatadriven.rebar.sql.client.websql;

import com.google.gwt.core.client.JavaScriptObject;

public final class WebSqlResultSetRow extends JavaScriptObject {
  
  protected WebSqlResultSetRow() {
    
  }
  
  public native String getString(String columnName) /*-{
    return this[columnName];
  }-*/;
  
  public native int getInt(String columnName) /*-{
    return this[columnName];
  }-*/;
  
  public native int getDouble(String columnName) /*-{
    return this[columnName];
  }-*/;
  
  public native boolean isNull(String columnName) /*-{
    return this[columnName] == null;
  }-*/;

}
