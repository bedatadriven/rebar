package com.bedatadriven.rebar.sql.client;

import java.util.Date;



public interface SqlResultSetRow {

	<X> X get(String columnName);
	<X> X getSingle();
		
  String getString(String columnName);
  String getSingleString();
  
  int getInt(String columnName);
  Integer getSingleInt();
  
  boolean getBoolean(String columnName);
  Boolean getSingleBoolean();
  
  double getDouble(String columnName);
  Double getSingleDouble();
  
  Date getDate(String columnName);

  boolean isNull(String columnName);
  
  
  

}
