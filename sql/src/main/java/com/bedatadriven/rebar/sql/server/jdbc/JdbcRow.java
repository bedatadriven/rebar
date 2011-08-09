package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class JdbcRow implements SqlResultSetRow {

  private Map<String, Object> values;

  public JdbcRow(ResultSet rs, String[] fieldNames) throws SQLException {
    values = new HashMap<String, Object>();
    for(int i=0;i!=fieldNames.length;++i) {
      Object value = rs.getObject(i+1);
      if(!rs.wasNull()) {
        values.put(fieldNames[i], value);
      }
    }
  }

  @Override
  public String getString(String columnName) {
    Object value = values.get(columnName);
    return value == null ? null : value.toString();
  }

  @Override
  public int getInt(String columnName) {
    return (int)getDouble(columnName);
  }

  @Override
  public double getDouble(String columnName) {
    Object value = values.get(columnName);
    if(value == null) {
      throw new NullPointerException(columnName);
    } else if(value instanceof Number) {
      return ((Number) value).doubleValue();
    } else {
      throw new UnsupportedOperationException("'" + columnName + "' is not numeric");
    }
  }

  @Override
  public boolean isNull(String columnName) {
    return values.containsKey(columnName);
  }
}
