package com.bedatadriven.rebar.sql.client;

import com.bedatadriven.rebar.sql.client.util.SqlKeyValueTable;
import com.google.gwt.user.client.rpc.AsyncCallback;




/**
 * A handle to an sql database.
 * 
 * @author alexander
 *
 */
public abstract class SqlDatabase {

	/**
	 * Begins an asynchronous transaction.
	 * 
	 * @param callback 
	 */
  public abstract void transaction(SqlTransactionCallback callback);
  
  
  public final void executeSql(final String statement, final AsyncCallback<Void> callback) {
  	transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql(statement);
			}

			@Override
      public void onError(SqlException e) {
				callback.onFailure(e);
      }

			@Override
      public void onSuccess() {
				callback.onSuccess(null);
      }
		});
  }
  
  public final void executeSql(final String statement) {
  	transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql(statement);
			}
		});
  }
  
  
  public final void selectSingleInt(final String statement, final AsyncCallback<Integer> callback) {
		transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql(statement, new SqlResultCallback() {

					@Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						callback.onSuccess(results.intResult());
          }
				});
			}

			@Override
      public void onError(SqlException e) {
	      callback.onFailure(e);
      }
		});
  }
  
  /**
   * Drops all tables in this database
   * 
   * @param callback called upon completion of the transaction
   */
  public final void dropAllTables(final AsyncCallback<Void> callback) {
  	transaction(new InnerSqlTxCallback<Void>(callback) {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql("select name from sqlite_master where type = 'table'", new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						for(SqlResultSetRow row : results.getRows()) {
							tx.executeSql("DROP TABLE " + row.getString("name"));
						}
					}
				});
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
  
  public SqlKeyValueTable keyValueTable(String tableName, String keyName, String valueName) {
  	return new SqlKeyValueTable(this, tableName, keyName, valueName);
  }
  
  public final void dropAllTables() {
  	dropAllTables(new NullCalback<Void>());
  }
  
  private static class NullCalback<T> implements AsyncCallback<T> {

		@Override
    public void onFailure(Throwable caught) {
	    
    }

		@Override
    public void onSuccess(T result) {
			
    }
  	
  	
  }
}