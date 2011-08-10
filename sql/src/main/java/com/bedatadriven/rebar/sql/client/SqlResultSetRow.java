package com.bedatadriven.rebar.sql.client;



public interface SqlResultSetRow {

	<X> X get(String columnName);
	<X> X getSingle();
		
  String getString(String columnName);
  String getSingleString();
  
  int getInt(String columnName);
  Integer getSingleInt();
  
  double getDouble(String columnName);
  Double getSingleDouble();

  boolean isNull(String columnName);
  
  
  

}
