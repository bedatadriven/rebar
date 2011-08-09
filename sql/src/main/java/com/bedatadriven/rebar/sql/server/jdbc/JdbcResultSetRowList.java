package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;

import java.util.List;

class JdbcResultSetRowList implements SqlResultSetRowList {

  private final List<JdbcRow> rows;

  public JdbcResultSetRowList(List<JdbcRow> rows) {
    this.rows = rows;
  }

  @Override
  public int length() {
    return rows.size();
  }

  @Override
  public SqlResultSetRow getRow(int index) {
    return rows.get(index);
  }
}
