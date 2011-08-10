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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlKeyValueTable<K, V> {

	private final SqlDatabase db;
	private String tableName;
	private String keyName;
	private String valueName;
	
	public SqlKeyValueTable(SqlDatabase db, final String tableName, final String keyName, final String valueName) {
		this.db = db;
		this.tableName = tableName;
		this.keyName = keyName;
		this.valueName = valueName;
		
		db.transaction(new SqlTransactionCallback() {
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("CREATE TABLE IF NOT EXISTS " + tableName + " (" + keyName + " NONE PRIMARY KEY, " + valueName + " NONE) ");
			}
		});
	}
	

	public void get(K key, final AsyncCallback<V> callback) {
		get(key, null, callback);
	}
	
	public void get(final K key, final V defaultValue, final AsyncCallback<V> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("SELECT " + valueName + " from " + tableName + " WHERE " + keyName + " = ?", new Object[] { key }, new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						if(results.getRows().isEmpty()) {
							callback.onSuccess(defaultValue);
						} else {
							callback.onSuccess((V)results.getRow(0).get(valueName));
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
	
	public void getAll(final AsyncCallback<Map<K,V>> callback) {
	db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("SELECT " + keyName + ", " + valueName + " from " + tableName + " WHERE " + valueName + " = ?", new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						Map<K,V> map = new HashMap<K,V>();
						for(SqlResultSetRow row : results.getRows()) {
							map.put(row.<K>get(keyName), row.<V>get(valueName));
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
	
	public void put(final K key, final V value, final AsyncCallback<Void> callback) {
		db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("INSERT OR REPLACE INTO " + tableName + " (" + keyName + ", " + valueName + ") VALUES ( ?, ? )", new Object[] { key, value } );
			}

			@Override
      public void onError(SqlException e) {
				callback.onFailure(e);
      }
		});
	}
	
	public void put(K key, V value) {
		put(key, value, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {	/* NOOP */ }
			
			@Override
			public void onFailure(Throwable caught) { /* NOOP */ }
		});
	}
	
	
	
}
