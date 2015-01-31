package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;

import java.util.Iterator;
import java.util.List;

class JdbcResultSetRowList implements SqlResultSetRowList {

  private final List<SqlResultSetRow> rows;

  public JdbcResultSetRowList(List<SqlResultSetRow> rows) {
    this.rows = rows;
  }

  @Override
  public Iterator<SqlResultSetRow> iterator() {
    return rows.iterator();
  }

  @Override
  public int size() {
    return rows.size();
  }

  @Override
  public SqlResultSetRow get(int index) {
    return rows.get(index);
  }

  @Override
  public boolean isEmpty() {
    return rows.isEmpty();
  }
}
