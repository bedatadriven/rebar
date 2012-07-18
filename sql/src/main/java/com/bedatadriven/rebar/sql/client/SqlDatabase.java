package com.bedatadriven.rebar.sql.client;

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.async.AsyncFunction;
import com.bedatadriven.rebar.sql.client.query.SqlDialect;
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
  
  /**
   * 
   * @return an AsyncFunction, which when applied returns a new 
   * transaction
   */
  public AsyncFunction<Void, SqlTransaction> transaction() {
  	return new AsyncFunction<Void, SqlTransaction>() {
			
			@Override
			protected void doApply(Void argument, final AsyncCallback<SqlTransaction> callback) {
				transaction(new SqlTransactionCallback() {
					
					@Override
					public void begin(SqlTransaction tx) {
						callback.onSuccess(tx);
					}

					@Override
          public void onError(SqlException e) {
	          callback.onFailure(e);
          }
				});
			}
		};
  }
  
  public abstract SqlDialect getDialect();

  /**
   * Executes a list of BulkOperation objects asynchronously within a transaction, such that
   * all of the statements fail or succeed together.
   *
   * On success, the callback will receive the total number of update rows.
   *
   * @param bulkOperationJsonArray
   * @param callback
   */
  public abstract void executeUpdates(String bulkOperationJsonArray, AsyncCallback<Integer> callback);
  
  /**
   * 
   * @return the name of the database
   */
  public abstract String getName();
  
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
  public final void dropAllTables(SqlTransaction tx) {
			tx.executeSql("select name from sqlite_master where type = 'table'", new SqlResultCallback() {
				
				@Override
				public void onSuccess(SqlTransaction tx, SqlResultSet results) {
					for(SqlResultSetRow row : results.getRows()) {
						String tableName = row.getString("name");
						// some implementations may store metadata in tables that cannot be deleted,
						// __WebKitMetadata__ for example
						if(!tableName.startsWith("_") && !tableName.startsWith("sqlite_")) {
							Log.debug("Dropping table " + tableName);
							tx.executeSql("DROP TABLE " + tableName, new SqlResultCallback() {

								@Override
                public void onSuccess(SqlTransaction tx, SqlResultSet results) {
									// noop
                }

								@Override
                public boolean onFailure(SqlException e) {
									// ignore other errors; there may be protected tables introduced
									// by other implementations
                  return SqlResultCallback.CONTINUE;
                }
							});
						}
					}
				}
			});
  }
  
  public SqlKeyValueTable keyValueTable(String tableName, String keyName, String valueName) {
  	return new SqlKeyValueTable(this, tableName, keyName, valueName);
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