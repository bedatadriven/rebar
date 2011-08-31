package com.bedatadriven.rebar.sql.rebind;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

public class ColumnAccessors {

	
	private final ColumnAccessor INTEGER = new ColumnAccessor() {
		
		@Override
		public Object get(SqlResultSetRow row, String columnName) {
			return row.getInt(columnName);
		}
	}; 
	
	private final ColumnAccessor STRING = new ColumnAccessor() {
		
		@Override
		public Object get(SqlResultSetRow row, String columnName) {
			return row.getString(columnName);
		}
	};
	
	private final ColumnAccessor DOUBLE = new ColumnAccessor() {
		
		@Override
		public Object get(SqlResultSetRow row, String columnName) {
			return row.getDouble(columnName);
		}
	};
	
	private final ColumnAccessor DATE = new ColumnAccessor() {
		
		@Override
		public Object get(SqlResultSetRow row, String columnName) {
			return row.getDate(columnName);
		}
	};
	
	private final ColumnAccessor BOOLEAN = new ColumnAccessor() {
		
		@Override
		public Object get(SqlResultSetRow row, String columnName) {
			return row.getBoolean(columnName);
		}
	};
	
	private static Map<Class, ColumnAccessor> accessors = new HashMap<Class, ColumnAccessor>();
	
	private ColumnAccessors() {
		accessors.put(Integer.TYPE, INTEGER);
		accessors.put(Integer.class, INTEGER);
		accessors.put(Double.TYPE, DOUBLE);
		accessors.put(Double.class, DOUBLE);
		accessors.put(Boolean.TYPE, BOOLEAN);
		accessors.put(Boolean.class, BOOLEAN);
		accessors.put(String.class, STRING);
		accessors.put(Date.class, DATE);
	}
	
	private static ColumnAccessors INSTANCE;
	
	public static ColumnAccessor get(Class clazz) {
		if(INSTANCE == null) {
			INSTANCE = new ColumnAccessors();
		}
		return INSTANCE.accessors.get(clazz);
	}
	
	public static boolean has(Class clazz) {
		if(INSTANCE == null) {
			INSTANCE = new ColumnAccessors();
		}
		return INSTANCE.accessors.containsKey(clazz);
		
	}
}
