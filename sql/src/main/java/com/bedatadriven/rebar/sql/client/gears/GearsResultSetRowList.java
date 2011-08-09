package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultSetRow;
import com.google.gwt.gears.client.database.ResultSet;

import java.util.ArrayList;

class GearsResultSetRowList implements SqlResultSetRowList {

  private final ArrayList<WebSqlResultSetRow> rows;

  public GearsResultSetRowList(ResultSet rs) {
    rows = new ArrayList<WebSqlResultSetRow>();
    while(rs.isValidRow()) {
      rows.add(createRow(rs));
      rs.next();
    }
  }

  @Override
  public int length() {
    return rows.size();
  }

  @Override
  public SqlResultSetRow getRow(int index) {
    return rows.get(index);
  }

  private static native WebSqlResultSetRow createRow(ResultSet rs) /*-{
    var row = {};
    var fieldCount = rs.fieldCount();
    for(var i=0;i!=fieldCount;++i) {
      row[rs.fieldName(i)] =rs.field(i);
    }
    return row;
  }-*/;
}
