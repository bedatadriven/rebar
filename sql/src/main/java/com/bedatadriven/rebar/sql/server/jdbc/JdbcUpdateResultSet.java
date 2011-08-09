package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

class JdbcUpdateResultSet implements SqlResultSet {

  private int insertId = -1;
  private int rowsAffected;

  public JdbcUpdateResultSet(Statement stmt) throws SQLException {
    rowsAffected = stmt.getUpdateCount();
    ResultSet rs = stmt.getGeneratedKeys();
    try {
      if(rs.next()) {
        insertId = rs.getInt(1);
        if(rs.wasNull()) {
          insertId = -1;
        }
      }
    } finally {
      try { rs.close(); } catch(Exception ignored) {}
    }
  }

  @Override
  public int getInsertId() {
    if(insertId == -1) {
      throw new SqlException("no rows were inserted");
    }
    return insertId;
  }

  @Override
  public int getRowsAffected() {
    return rowsAffected;
  }

  @Override
  public SqlResultSetRowList getRows() {
    return new JdbcResultSetRowList(Collections.<JdbcRow>emptyList());
  }
}
