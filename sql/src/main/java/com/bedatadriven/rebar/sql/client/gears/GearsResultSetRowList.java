package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultSetRow;
import com.google.gwt.gears.client.database.ResultSet;

import java.util.ArrayList;
import java.util.Iterator;

class GearsResultSetRowList implements SqlResultSetRowList {

  private final ArrayList<SqlResultSetRow> rows;

  public GearsResultSetRowList(ResultSet rs) {
    rows = new ArrayList<SqlResultSetRow>();
    while(rs.isValidRow()) {
      rows.add(createRow(rs));
      rs.next();
    }
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
  public Iterator<SqlResultSetRow> iterator() {
	  return rows.iterator();
  }

	private static native WebSqlResultSetRow createRow(ResultSet rs) /*-{
    var row = {};
    var fieldCount = rs.fieldCount();
    for(var i=0;i!=fieldCount;++i) {
      row[rs.fieldName(i)] =rs.field(i);
    }
    return row;
  }-*/;

	@Override
  public boolean isEmpty() {
	  return rows.isEmpty();
  }
}
