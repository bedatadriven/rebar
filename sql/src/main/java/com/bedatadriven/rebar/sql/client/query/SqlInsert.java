package com.bedatadriven.rebar.sql.client.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SqlInsert {
	
	private String tableName;
	private List<Object> values = new ArrayList<Object>();
	private List<String> columns = new ArrayList<String>();

	public SqlInsert(String tableName) {
		this.tableName = tableName;
	}

	public static SqlInsert insertInto(String tableName) {
		return new SqlInsert(tableName);
	}
	
	public SqlInsert value(String columnName, Object value) {
		if(value != null) {
			columns.add(columnName);
			values.add(value);
		}
		return this;
	}
	
	private String sql() {
		StringBuilder sql = new StringBuilder("INSERT INTO ")
			.append(tableName)
			.append(" (");
		
		for(int i=0;i!=columns.size();++i) {
			if(i>0) {
				sql.append(", ");
			}
			sql.append(columns.get(i));
		}
		sql.append(") VALUES (");
		
		for(int i=0;i!=columns.size();++i) {
			if(i>0) {
				sql.append(", ");
			}
			sql.append("?");
		}
		sql.append(")");
		return sql.toString();
	}

	private Object[] params() {
	  return values.toArray( new Object[values.size()] );
  }
	
	public void execute(SqlTransaction tx) {
		if(!values.isEmpty()) {
			tx.executeSql(sql(), params());
		}
	}
	
	public void execute(SqlTransaction tx, final AsyncCallback<Integer> callback) {
		if(values.isEmpty()) {
			callback.onSuccess(0);
		} else {
			tx.executeSql(sql(), params(), new SqlResultCallback() {
				
				@Override
				public void onSuccess(SqlTransaction tx, SqlResultSet results) {
					callback.onSuccess(results.getRowsAffected());
				}
			});
		}
	}

	public void execute(SqlDatabase database, final AsyncCallback<Integer> callback) {
		if(values.isEmpty()) {
			callback.onSuccess(0);
		} else {
			database.transaction(new SqlTransactionCallback() {
				
				@Override
				public void begin(SqlTransaction tx) {
					tx.executeSql(sql(), params(), new SqlResultCallback() {
						
						@Override
						public void onSuccess(SqlTransaction tx, SqlResultSet results) {
							callback.onSuccess(results.getRowsAffected());
						}
					});
				}

				@Override
        public void onError(SqlException e) {
					callback.onFailure(e);
        }
			});
		}
	}
	
}