package com.bedatadriven.rebar.sql.server.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

class JdbcRow implements SqlResultSetRow {

	private int valueCount;
  private Map<String, Object> values;

  public JdbcRow(ResultSet rs, String[] fieldNames) throws SQLException {
    valueCount = fieldNames.length;
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
  public <X> X get(String columnName) {
	  return (X)values.get(columnName);
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

	@Override
  public String getSingleString() {
		assertSingleColumn();
		if(values.isEmpty()) {
			return null;
		} else {
			return values.values().iterator().next().toString();
		}
  }

	@Override
  public Integer getSingleInt() {
		assertSingleColumn();
		if(values.isEmpty()) {
			return null;
		} else {
			return ((Number)values.values().iterator().next()).intValue();
		}
  }

	@Override
  public Double getSingleDouble() {
		assertSingleColumn();
		if(values.isEmpty()) {
			return null;
		} else {
			return ((Number)values.values().iterator().next()).doubleValue();
		}
  }
	
	@Override
  public <X> X getSingle() {
	  assertSingleColumn();
		if(values.isEmpty()) {
			return null;
		} else {
			return (X) values.values().iterator().next();
		}
  }

	private void assertSingleColumn() {
		if(valueCount != 1) {
			throw new IllegalStateException("getSingleXXX() can only be called when the row has exactly one column; this row has " + 
						valueCount + " columns");
		}
	}


  
}
