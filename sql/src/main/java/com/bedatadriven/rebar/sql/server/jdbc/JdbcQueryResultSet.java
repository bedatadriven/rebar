package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class JdbcQueryResultSet implements SqlResultSet {

  private SqlResultSetRowList rows;

  JdbcQueryResultSet(Statement stmt) throws SQLException {
    ResultSet rs = stmt.getResultSet();
    ResultSetMetaData metaData = rs.getMetaData();

    String[] fieldNames = new String[metaData.getColumnCount()];
    for(int i=0;i!=fieldNames.length;++i) {
      fieldNames[i] = metaData.getColumnName(i+1);
    }

    List<JdbcRow> rows = new ArrayList<JdbcRow>();
    while(rs.next()) {
      rows.add(new JdbcRow(rs, fieldNames));
    }

    this.rows = new JdbcResultSetRowList(rows);
  }

  @Override
  public int getInsertId() {
    throw new SqlException("no rows were inserted");
  }

  @Override
  public int getRowsAffected() {
    return 0;
  }

  @Override
  public SqlResultSetRowList getRows() {
    return rows;
  }
}
