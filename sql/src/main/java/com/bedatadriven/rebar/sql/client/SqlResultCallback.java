package com.bedatadriven.rebar.sql.client;


public interface SqlResultCallback {
  
  void onSuccess(SqlTransaction tx, SqlResultSet results);
  
  /**
   * Called if there is an error while executing the statement.
   * 
   * @param e the exception
   * 
   * @return true, if the transaction should continue, or false if the transaction should be aborted
   */
  boolean onFailure(SqlException e);
  
}
