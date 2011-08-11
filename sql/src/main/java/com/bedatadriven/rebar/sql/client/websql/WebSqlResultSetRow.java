package com.bedatadriven.rebar.sql.client.websql;

import java.util.Date;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.google.gwt.core.client.JavaScriptObject;

public final class WebSqlResultSetRow extends JavaScriptObject implements SqlResultSetRow {

  protected WebSqlResultSetRow() {

  }
	
  @Override
  public native <X> X get(String columnName) /*-{
	  return this[columnName];
  }-*/;
    
  
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
  

  /**
   * 
   * @param ci (zero-based) column index
   * @return the name of the column at index {@code ci}
   */
  private native String firstColumnName() /*-{
  	for(var pn in this) {
  		return pn;
		}
		return null;
  }-*/;

	@Override
  public String getSingleString() {
	  return getString(firstColumnName());
  }

	@Override
  public Integer getSingleInt() {
	  return getInt(firstColumnName());
  }

	@Override
  public Double getSingleDouble() {
	  return getDouble(firstColumnName());
  }

	@Override
  public <X> X getSingle() {
	  return this.<X>get(firstColumnName());
  }

	@Override
  public Date getDate(String columnName) {
	  return new Date((long)getDouble(columnName));
  }

  
}
