package com.bedatadriven.rebar.sql.server.jdbc;

import java.sql.Connection;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface JdbcConnectionProvider {

	/**
	 * Obtains a connection.
	 * 
	 * @param callback
	 */
	void getConnection(AsyncCallback<Connection> callback);
	
	
}
