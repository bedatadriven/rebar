package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;

class Counter implements WebSqlResultCallback {

	private int rowsAffected = 0;
	
	@Override
  public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
		rowsAffected = results.safeGetRowsAffected();
  }

	@Override
  public boolean onFailure(WebSqlException e) {
	  return SqlResultCallback.ABORT;
  }

	public int getRowsAffected() {
  	return rowsAffected;
  }
}
