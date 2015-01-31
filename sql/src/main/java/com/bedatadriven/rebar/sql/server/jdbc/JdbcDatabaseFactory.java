package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;

public class JdbcDatabaseFactory implements SqlDatabaseFactory {

  public JdbcDatabaseFactory() {
  }

  @Override
  public SqlDatabase open(String databaseName) {
    return new JdbcDatabase(databaseName);
  }
}
