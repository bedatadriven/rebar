package com.bedatadriven.rebar.sql.client;

/**
 * Common callback interface for asynchronous SQL results
 */
public abstract class SqlResultCallback {

    public abstract void onSuccess(SqlTransaction tx, SqlResultSet results);

    /**
     * Called if there is an error while executing the statement.
     *
     * @param e the exception
     *
     * @return true, if the transaction should continue, or false if the transaction should be aborted and the transaction's 
     * error handler called. 
     */
    public boolean onFailure(SqlException e) {
    	return false;
    }


}
