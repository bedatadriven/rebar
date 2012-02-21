package com.bedatadriven.rebar.sql.client.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;

public class SqlUpdate {
	private String tableName;
	private String action;
	private List<Object> values = new ArrayList<Object>();
	private List<String> columns = new ArrayList<String>();

	private StringBuilder where = new StringBuilder();
	private List<Object> whereParameters = new ArrayList<Object>();

	private static String UPDATE = "UPDATE";
	private static String DELETE = "DELETE FROM";

	private SqlUpdate(String action, String tableName) {
		this.action = action;
		this.tableName = tableName;
	}

	public static SqlUpdate update(String tableName) {
		return new SqlUpdate(UPDATE, tableName);
	}

	public static SqlUpdate delete(String tableName) {
		return new SqlUpdate(DELETE, tableName);
	}

	public SqlUpdate where(String columnName, Object value) {
		if(where.length() > 0) {
			where.append(" AND ");
		} 
		where.append(columnName).append("=?");
		whereParameters.add(value);

		return this;
	}

	public SqlUpdate value(String columnName, Object value) {
		columns.add(columnName);
		values.add(value);
		return this;
	}
	
	public SqlUpdate valueIfNotNull(String columnName, Object value) {
		if(value != null) {
			value(columnName, value);
		}
		return this;
	}

	public SqlUpdate value(String columnName, Map<String, Object> properties, String propertyName) {
		if(properties.containsKey(propertyName)) {
			columns.add(columnName);
			values.add(properties.get(propertyName));
		}
		return this;
	}

	public SqlUpdate value(String columnName, Map<String, Object> properties) {
		return value(columnName, properties, columnName);
	}

	private Object[] params() {
		return values.toArray(new Object[values.size()]);
	}
	
	public void execute(SqlTransaction tx) {
		execute(tx, null);
	}

	public void execute(SqlTransaction tx, SqlResultCallback callback) {

		if(where.length() == 0) {
			throw new RuntimeException("Where clause not specified");
		}

		if(UPDATE.equals(action) && values.isEmpty()) {
			return; // nothing to do.
		}


		StringBuilder sql = new StringBuilder(action)
		.append(" ")
		.append(tableName);


		if(UPDATE.equals(action)) {
			sql.append(" SET ");

			for(int i=0;i!=columns.size();++i) {
				if(i>0) {
					sql.append(", ");
				}
				sql.append(columns.get(i)).append("=?");
			}
		}

		sql.append(" WHERE ").append(where.toString());

		Object [] params = new Object[values.size() + whereParameters.size()];
		
		int nextParamIndex = 0;
		for(Object value : values) {
			params[nextParamIndex++] = value;
		}
		for(Object param : whereParameters) {
			params[nextParamIndex++] = param;
		}
		
		if(callback == null) {
			tx.executeSql(sql.toString(), params);
		} else {
			tx.executeSql(sql.toString(), params, callback);
		}
	}
}
