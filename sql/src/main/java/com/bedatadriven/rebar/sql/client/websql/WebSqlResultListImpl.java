package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;

import java.util.Iterator;

class WebSqlResultListImpl implements SqlResultSetRowList {

  private final WebSqlResultSetRowList list;

  public WebSqlResultListImpl(WebSqlResultSetRowList list) {
    super();
    this.list = list;
  }

  @Override
  public Iterator<SqlResultSetRow> iterator() {
    return new RowIterator();
  }

  @Override
  public int size() {
    return list.length();
  }

  @Override
  public SqlResultSetRow get(int index) {
    return list.getRow(index);
  }

  @Override
  public boolean isEmpty() {
    return list.length() == 0;
  }

  private class RowIterator implements Iterator<SqlResultSetRow> {

    private int i = 0;

    @Override
    public boolean hasNext() {
      return i < list.length();
    }

    @Override
    public SqlResultSetRow next() {
      return list.getRow(i++);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

}
