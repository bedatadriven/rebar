package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.time.calendar.LocalDate;

import java.sql.*;

public class SqliteExecutor extends JdbcExecutor {

  private String connectionUrl;

  private Connection conn;

  public SqliteExecutor(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  @Override
  public SqlResultSet execute(String statement, Object[] params)
      throws Exception {
    return doExecute(conn, statement, params);
  }

  @Override
  public final boolean begin() throws Exception {
    conn = openConnection();

    try {
      boolean available = doBeginTransaction();
      if (!available) {
        closeConnectionIgnoringAnyExceptions();
      }
      return available;

    } catch (SQLException e) {
      closeConnectionIgnoringAnyExceptions();
      throw e;
    }
  }


  private void closeConnectionIgnoringAnyExceptions() {
    try {
      conn.close();
    } catch (SQLException ignored) {
      // ignore
    }
  }

  private Connection openConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    return DriverManager.getConnection(connectionUrl);
  }

  private boolean doBeginTransaction() throws SQLException {
    try {
      Statement stmt = conn.createStatement();
      stmt.execute("BEGIN EXCLUSIVE TRANSACTION");
      stmt.close();

      return true;

    } catch (SQLException e) {
      if (e.getMessage().contains("[SQLITE_BUSY]")) {
        return false; // database is locked; attempt will be rescheduled
      } else {
        throw e; // some other fatal error
      }
    }
  }

  private void doCommit() throws SQLException {
    Statement stmt = conn.createStatement();
    stmt.execute("END TRANSACTION");
    stmt.close();
  }

  @Override
  protected void setParam(PreparedStatement stmt, Object[] params, int i)
      throws SQLException {
    if (params[i] instanceof java.util.Date) {
      stmt.setDouble(i + 1, ((java.util.Date) params[i]).getTime());
    } else if (params[i] instanceof LocalDate) {
      stmt.setString(i + 1, ((LocalDate) params[i]).toString());
    } else {
      stmt.setObject(i + 1, params[i]);
    }
  }

  @Override
  public void rollback() throws Exception {
    Statement stmt = conn.createStatement();
    stmt.execute("ROLLBACK TRANSACTION");
    stmt.close();
  }

  @Override
  public final void commit() throws Exception {
    try {
      doCommit();
    } finally {
      conn.close();
    }
  }
}
