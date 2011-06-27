package com.bedatadriven.rebar.sql.server.sqlite;

import java.sql.Connection;
import java.sql.Statement;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;

public class SqlJdbcTransaction implements SqlTransaction {
  
  private Connection connection;
  

  public SqlJdbcTransaction(Connection connection) {
    this.connection = connection;
  }


  @Override
  public void executeSql(String statement) {

  }


  @Override
  public void executeSql(String statement, Object[] parameters) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void executeSql(String statement, Object[] parameters,
      SqlResultCallback callback) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void executeSql(String statement, SqlResultCallback resultCallback) {
    // TODO Auto-generated method stub
    
  }
  

}
