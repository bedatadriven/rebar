package com.bedatadriven.rebar.sql.client.util;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;

public abstract class RowHandler extends SqlResultCallback {

	@Override
  public final void onSuccess(SqlTransaction tx, SqlResultSet results) {
	  for(SqlResultSetRow row : results.getRows()) {
	  	handleRow(row);
	  }
  }

	public abstract void handleRow(SqlResultSetRow row);
	
}
