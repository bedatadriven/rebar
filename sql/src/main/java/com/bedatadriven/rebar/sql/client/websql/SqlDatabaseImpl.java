package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;

class SqlDatabaseImpl extends SqlDatabase {
		
	private final WebSqlDatabase db;
	
	 public SqlDatabaseImpl(WebSqlDatabase db) {
	  super();
	  this.db = db;
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
	
}
