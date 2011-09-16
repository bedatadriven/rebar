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

  public native boolean getBoolean(String columnName) /*-{
    return Boolean(this[columnName]);
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
		if(isNull(columnName)) {
			return null;
		} else {
		  return new Date((long)getDateAsMillis(columnName));			
		}
  }

	@Override
  public Boolean getSingleBoolean() {
		return getBoolean(firstColumnName());
  }
	
	private native double getDateAsMillis(String columnName) /*-{
		var x = this[columnName];
		if(!isNaN(x)) {
			return x;
		} else if(typeof x == "string" && x.charAt(4)=='-') {
			var d = new Date(x.substring(0,4), 
											 x.substring(5,7)-1,
											 x.substring(8,10));
			return d.getTime();
		} else {
			return -1;
		}
	}-*/;
  
}
