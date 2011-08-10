package com.bedatadriven.rebar.sql.client;

public abstract class SqlTransactionCallback {
  
  public abstract void begin(SqlTransaction tx);
  
  public void onError(SqlException e) {
  	
  }
  
  public void onSuccess() {
  	
  }
}
