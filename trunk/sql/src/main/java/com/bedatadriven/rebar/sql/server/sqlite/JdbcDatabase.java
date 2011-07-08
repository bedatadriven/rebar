package com.bedatadriven.rebar.sql.server.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;

public class JdbcDatabase implements SqlDatabase {
  private final String name;

  public JdbcDatabase(String name) {
    this.name = name;
  }
 
  
  @Override
  public void transaction(SqlTransactionCallback callback) {
    Connection connection = null;
    try
    {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
      connection.setAutoCommit(false);
      callback.begin(new SqlJdbcTransaction(connection));
    }
    catch(SQLException e)
    {
      if(connection != null) {
        try {
          connection.rollback();
          connection.close();
        } catch (SQLException ignored) {
        }
      }
      callback.onError(new SqlException(e));
    }
  }

}
