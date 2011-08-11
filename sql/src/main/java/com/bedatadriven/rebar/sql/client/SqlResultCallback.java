package com.bedatadriven.rebar.sql.client;

/**
 * Common callback interface for asynchronous SQL results
 */
public abstract class SqlResultCallback {

		public static final boolean CONTINUE = false;
		public static final boolean ABORT = true;
	
    public abstract void onSuccess(SqlTransaction tx, SqlResultSet results);

    /**
     * Called if there is an error while executing the statement.
     *
     * @param e the exception
     *
     * @return {@code ABORT} ({@code false}), if the transaction should continue, or {@code CONTINUE} (@{code true}) if the 
     * transaction should be aborted and the transaction's 
     * error handler called. 
     */
    public boolean onFailure(SqlException e) {
    	return ABORT;
    }


}
