package com.bedatadriven.rebar.sql.client.websql;

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.bulk.PreparedStatementBatch;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.rpc.AsyncCallback;

class SqlDatabaseImpl extends SqlDatabase {
		
	private final String name;
	private final WebSqlDatabase db;
	
	public SqlDatabaseImpl(String name) {
	  super();
	  this.name = name;
	  this.db = WebSqlDatabase.openDatabase(name, WebSqlDatabase.ANY_VERSION, name, WebSqlDatabase.DEFAULT_SIZE);
  }


	@Override
  public String getName() {
	  return name;
  }


	/**
   * Begins an asynchronous transaction using the common SQL API
   *
   * @param callback
   */
  @Override
  public void transaction(final SqlTransactionCallback callback) {
    db.transaction(new WebSqlTransactionCallback() {
      
      @Override
      public void onError(WebSqlException e) {
        callback.onError(e);
      }
      
      @Override
      public void begin(WebSqlTransaction tx) {
        callback.begin(tx);        
      }

			@Override
      public void onSuccess() {
				callback.onSuccess();
	    }
    });
  }

  @Override
  public void executeUpdates(final String bulkOperationJsonArray,
                             final AsyncCallback<Integer> callback) {

   
  	try {
  		
  		final Counter counter = new Counter();
	
	    db.transaction(new WebSqlTransactionCallback() {
				
			
				@Override
				public void begin(WebSqlTransaction tx) {
			     Log.debug("WebSqlBulkUpdater about to call eval on '" + bulkOperationJsonArray.substring(0, 600) + "'");
			      JsArray<PreparedStatementBatch> statements = PreparedStatementBatch.fromJson(bulkOperationJsonArray);
			      
			      Log.debug("WebSqlBulkUpdater queuing " + statements.length() + " statements");
			      
			      for(int i=0;i!=statements.length();++i) {
			      	PreparedStatementBatch batch = statements.get(i);
				      Log.debug("WebSqlBulkUpdater queuing statement [" + batch.getStatement() + "]");

			        JsArray<JsArrayString> executions = batch.getExecutions();
							if(executions == null || executions.length() == 0) {
			          // simple statement with no parameters
								tx.executeSql(batch.getStatement(), counter);
			        
							} else {
								// statement to be executed several times with different parameters
			        	for(int j=0;j!=executions.length(); ++j) {
			        		tx.executeSql(batch.getStatement(), executions.get(j), counter);
			        	}
			        }
			      }
				}
				
				@Override
				public void onSuccess() {
					callback.onSuccess(counter.getRowsAffected());
				}
				
				@Override
				public void onError(WebSqlException e) {
					callback.onFailure(e);
				}
			});
  	} catch(Exception e) {
  		callback.onFailure(e);
  	}
  }
 
}
