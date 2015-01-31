package com.bedatadriven.rebar.sql.server.jdbc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.sql.Connection;


public interface JdbcConnectionProvider {

  /**
   * Obtains a connection.
   *
   * @param callback
   */
  void getConnection(AsyncCallback<Connection> callback);


}
