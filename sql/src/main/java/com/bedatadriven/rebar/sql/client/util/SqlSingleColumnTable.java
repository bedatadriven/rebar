package com.bedatadriven.rebar.sql.client.util;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlSingleColumnTable<T> {
	
	private final SqlDatabase db;
	private final String tableName;
	private final String columnName;
	
	
	public SqlSingleColumnTable(SqlDatabase db, final String tableName, final String columnName) {
	  super();
	  this.db = db;
	  this.tableName = tableName;
	  this.columnName = columnName;
	  
		db.transaction(new SqlTransactionCallback() {
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnName + " NONE)");
			}
		});
  }
		
	public final void put(final T value, final AsyncCallback<Void> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("delete from " + tableName);
				tx.executeSql("insert into " + tableName + " (" + columnName + ") VALUES (?)", 
						new Object[] {convertToParameter(value)} );
			}

			@Override
      public void onError(SqlException e) {
	      callback.onFailure(e);
      }
		});
	}
	
	public final void get(final AsyncCallback<T> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("select " + columnName + " FROM " + tableName, new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						if(results.getRows().isEmpty()) {
							callback.onSuccess(null);
						} else {
							callback.onSuccess(convertFromResultSet(results));
						}	
					}
				});
			}

			@Override
      public void onError(SqlException e) {
	      callback.onFailure(e);
      }
		});
		
	}

	protected T convertFromResultSet(SqlResultSet results) {
	  return (T)results.getRow(0).get(columnName);
  }

	protected Object convertToParameter(final T value) {
	  return value;
  }
}
