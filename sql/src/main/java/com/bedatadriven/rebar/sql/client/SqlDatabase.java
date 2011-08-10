package com.bedatadriven.rebar.sql.client;




/**
 * A handle to an sql database.
 * 
 * @author alexander
 *
 */
public interface SqlDatabase {

	/**
	 * Begins an asynchronous transaction.
	 * 
	 * @param callback 
	 */
  void transaction(SqlTransactionCallback callback);

  
  
}