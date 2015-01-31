package com.bedatadriven.rebar.sql.client.util;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;

public abstract class SingleRowHandler extends SqlResultCallback {

  @Override
  public final void onSuccess(SqlTransaction tx, SqlResultSet results) {
    if (results.getRows().size() != 1) {
      throw new AssertionError("Expected exactly one row, received " + results.getRows().size());
    }
    handleRow(results.getRow(0));
  }

  public abstract void handleRow(SqlResultSetRow row);

}
