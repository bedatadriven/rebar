package com.bedatadriven.rebar.sql.client.util;

import java.util.HashMap;
import java.util.Map;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.fn.AsyncSql;
import com.bedatadriven.rebar.sql.client.fn.QueryFunction;
import com.bedatadriven.rebar.sql.client.fn.TxAsyncFunction;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlKeyValueTable {

	private final SqlDatabase db;
	private String tableName;
	private String keyName;
	private String valueName;
	
	public SqlKeyValueTable(SqlDatabase db, final String tableName, final String keyName, final String valueName) {
		this.db = db;
		this.tableName = tableName;
		this.keyName = keyName;
		this.valueName = valueName;
	}
	
	public TxAsyncFunction<Void, Void> createTableIfNotExists() {
		return AsyncSql.ddl("CREATE TABLE IF NOT EXISTS " + tableName + " (" + keyName + " TEXT PRIMARY KEY, " + valueName + " TEXT) ");
	}
	
	public void get(String key, final AsyncCallback<String> callback) {
		get(key, null, callback);
	}
	
	public void get(final String key, final String defaultValue, final AsyncCallback<String> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("SELECT " + valueName + " from " + tableName + " WHERE " + keyName + " = ?", new Object[] { key }, new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						if(results.getRows().isEmpty()) {
							callback.onSuccess(defaultValue);
						} else {
							callback.onSuccess(results.getRow(0).getString(valueName));
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
	
	public void getAll(final AsyncCallback<Map<String, String>> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("SELECT " + keyName + ", " + valueName + " from " + tableName + " WHERE " + valueName + " = ?", new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						Map<String, String> map = new HashMap<String, String>();
						for(SqlResultSetRow row : results.getRows()) {
							map.put(row.getString(keyName), row.getString(valueName));
						}
						callback.onSuccess(map);
					}
				});
			}

			@Override
      public void onError(SqlException e) {
				callback.onFailure(e);
      }
		});
		
	}
	
	public void put(final String key, final String value, final AsyncCallback<Void> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("INSERT OR REPLACE INTO " + tableName + " (" + keyName + ", " + valueName + ") VALUES ( ?, ? )", new Object[] { key, value } );
			}

			@Override
      public void onSuccess() {
				callback.onSuccess(null);
      }

			@Override
      public void onError(SqlException e) {
				callback.onFailure(e);
      }
		});
	}
	
	public void put(String key, String value) {
		put(key, value, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {	/* NOOP */ }
			
			@Override
			public void onFailure(Throwable caught) { /* NOOP */ }
		});
	}
	
	
	
}
