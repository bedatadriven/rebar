package com.bedatadriven.rebar.sql.client;

public interface SqlTransactionCallback {
  
  void begin(SqlTransaction tx);
  
  void onError(SqlException e);
  
  void onSuccess();
}
