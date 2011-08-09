package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;
import com.google.gwt.core.client.testing.StubScheduler;

class JdbcDatabase implements SqlDatabase {

  private final String databaseName;

  public JdbcDatabase(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public void transaction(SqlTransactionCallback callback) {
    new SyncTransactionAdapter(new JdbcExecutor("jdbc:sqlite:" + databaseName),
        new SyncScheduler(), callback);
  }
}
