package com.bedatadriven.rebar.sql.server.jdbc;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

class JdbcRow implements SqlResultSetRow {

	private int valueCount;
  private Map<String, Object> values;

  public JdbcRow(ResultSet rs, String[] fieldNames) throws SQLException {
    valueCount = fieldNames.length;
  	values = new HashMap<String, Object>();
    for(int i=0;i!=fieldNames.length;++i) {
      Object value = rs.getObject(i+1);
      if(value != null && !rs.wasNull()) {
        values.put(fieldNames[i], value);
      } 
    }
  }

  @Override
  public String getString(String columnName) {
    Object value = values.get(columnName);
    if(value == null) {
    	return null;
    } else if(value instanceof String) {
    	return (String)value;
    } else if(value instanceof Number || value instanceof Boolean) {
    	return value.toString();
    } else if(value instanceof Clob) {
    	try {
    		return readString( ((Clob) value).getCharacterStream() );
    	} catch(Exception e) {
    		throw new RuntimeException("Exception reading string from column '" + columnName + "'", e);
    	}
    } else {
    	throw new RuntimeException("Error get string value of '" + columnName + "', value is of class " + value.getClass().getName());
    }
  }

  private String readString(Reader in) throws IOException {
	
  	StringBuilder sb = new StringBuilder();
  	char[] buf = new char[1024];
  	int charsRead;
  	while( (charsRead = in.read(buf)) != -1) {
  		sb.append(buf, 0, charsRead);
  	}
  	return sb.toString();
  }

	@Override
  public int getInt(String columnName) {
    Object value = values.get(columnName);
    if(value == null) {
      throw new NullPointerException(missingColumnMessage(columnName));
    } else if(value instanceof Number) {
      return ((Number) value).intValue();
    } else  {
    	return Integer.parseInt(value.toString());
    }
  }

	private String missingColumnMessage(String columnName) {
	  return "Column " + columnName + " not found. Columns present: " + values.keySet().toString();
  }
  
	@Override
  public <X> X get(String columnName) {
	  return (X)values.get(columnName);
  }

  @Override
  public double getDouble(String columnName) {
    Object value = values.get(columnName);
    if(value == null) {
      throw new NullPointerException(missingColumnMessage(columnName));
    } else if(value instanceof Number) {
      return ((Number) value).doubleValue();
    } else  {
    	return Double.parseDouble(value.toString());
    }
  }
  
  

  @Override
  public boolean getBoolean(String columnName) {
  	Object value = values.get(columnName);
    if(value == null) {
      throw new NullPointerException(missingColumnMessage(columnName));
    } else if(value instanceof Boolean) {
    	return ((Boolean) value);
    } else if(value instanceof Number) {
      return ((Number) value).intValue() != 0;
    } else  {
    	return Integer.parseInt(value.toString()) != 0;
    } 
  }

	@Override
  public Boolean getSingleBoolean() {
		assertSingleColumn();
		if(values.isEmpty()) {
			return null;
		} else {
			return ((Boolean)values.values().iterator().next());
		}
  }

	@Override
  public boolean isNull(String columnName) {
    return !values.containsKey(columnName);
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

	@Override
  public Date getDate(String columnName) {
	   Object value = values.get(columnName);
	    if(value == null) {
	      return null;
	    } else if(value instanceof java.util.Date) {
	    	return (java.util.Date)value;
	    } else if(value instanceof Number) {
	      return new Date(((Number) value).longValue());
	    } else  {
	    	try {
	    		String s = value.toString();
	    		// parse ISO8601 dates used by sqlite
	    		// see http://www.sqlite.org/lang_datefunc.html
	    		// TODO: implement times
	    		if(s.length() >= 10 && s.charAt(4) == '-') {
	    			Calendar cal = Calendar.getInstance();
	    			cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0,4)));
	    			cal.set(Calendar.MONTH, Integer.parseInt(s.substring(5,7))-1);
	    			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(8,10)));
	    			
	    			cal.set(Calendar.HOUR_OF_DAY, 0);
	    			cal.set(Calendar.MINUTE, 0);
	    			cal.set(Calendar.SECOND, 0);
	    			cal.set(Calendar.MILLISECOND, 0);
	    			
	    			return cal.getTime();
	    		} else {
	    			return new Date(Long.parseLong(s));
	    		}
        } catch (NumberFormatException e) {
	        throw new SqlException("Could not parse '" + value + "' as date", e);
        }
	    }  
	}

	private void assertSingleColumn() {
		if(valueCount != 1) {
			throw new IllegalStateException("getSingleXXX() can only be called when the row has exactly one column; this row has " + 
						valueCount + " columns");
		}
	}
}
