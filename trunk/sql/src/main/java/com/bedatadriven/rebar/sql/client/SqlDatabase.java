package com.bedatadriven.rebar.sql.client;

import com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback;

/**
 * A handle to an sql database.
 * 
 * @author alexander
 *
 */
public interface SqlDatabase {

  void transaction(SqlTransactionCallback callback);

}