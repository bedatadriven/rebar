package com.bedatadriven.rebar.sql.client;

import com.bedatadriven.rebar.sql.client.websql.ResultCallback;

public interface SqlTransaction {

  
  /**
   * Executes a single sql statement asynchronously.
   * 
   * @param statement an sql statement
   */
  void executeSql(String statement);

  
  /**
   * Executes a parameterized sql statement sql statement
   * 
   * @param statement an sql statement 
   * @param parameters positional parameters
   */
  void executeSql(String statement, Object[] parameters);

  
  /**
   * Executes a parameterized sql statement asynchronously
   * 
   * @param statement
   * @param parameters
   * @param callback
   */
  void executeSql(String statement, Object[] parameters,
      ResultCallback callback);

  
  /**
   * Executes an sql statement asynchronously.
   * @param statement
   * @param resultCallback
   */
  void executeSql(String statement,
      ResultCallback resultCallback);

}