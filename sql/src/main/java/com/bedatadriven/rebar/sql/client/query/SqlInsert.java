package com.bedatadriven.rebar.sql.client.query;

import com.bedatadriven.rebar.async.ChainedCallback;
import com.bedatadriven.rebar.sql.client.*;
import com.bedatadriven.rebar.sql.client.fn.TxAsyncFunction;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;


public class SqlInsert extends TxAsyncFunction<Void, Void> {

  private String tableName;
  private List<Object> values = new ArrayList<Object>();
  private List<String> columns = new ArrayList<String>();

  public SqlInsert(String tableName) {
    this.tableName = tableName;
  }

  public static SqlInsert insertInto(String tableName) {
    return new SqlInsert(tableName);
  }

  public SqlInsert value(String columnName, Object value) {
    if (value != null) {
      columns.add(columnName);
      values.add(value);
    }
    return this;
  }

  private String sql() {
    StringBuilder sql = new StringBuilder("INSERT INTO ")
        .append(tableName)
        .append(" (");

    for (int i = 0; i != columns.size(); ++i) {
      if (i > 0) {
        sql.append(", ");
      }
      sql.append(columns.get(i));
    }
    sql.append(") VALUES (");

    for (int i = 0; i != columns.size(); ++i) {
      if (i > 0) {
        sql.append(", ");
      }
      sql.append("?");
    }
    sql.append(")");
    return sql.toString();
  }

  private Object[] params() {
    return values.toArray(new Object[values.size()]);
  }

  public void execute(SqlTransaction tx) {
    if (!values.isEmpty()) {
      tx.executeSql(sql(), params());
    }
  }

  public void execute(SqlTransaction tx, final AsyncCallback<Integer> callback) {
    if (values.isEmpty()) {
      callback.onSuccess(0);
    } else {
      tx.executeSql(sql(), params(), new SqlResultCallback() {

        @Override
        public void onSuccess(SqlTransaction tx, SqlResultSet results) {
          callback.onSuccess(results.getRowsAffected());
        }
      });
    }
  }

  public void execute(SqlDatabase database, final AsyncCallback<Integer> callback) {
    if (values.isEmpty()) {
      callback.onSuccess(0);
    } else {
      database.transaction(new SqlTransactionCallback() {

        @Override
        public void begin(SqlTransaction tx) {
          tx.executeSql(sql(), params(), new SqlResultCallback() {

            @Override
            public void onSuccess(SqlTransaction tx, SqlResultSet results) {
              callback.onSuccess(results.getRowsAffected());
            }
          });
        }

        @Override
        public void onError(SqlException e) {
          callback.onFailure(e);
        }
      });
    }
  }

  @Override
  protected void doApply(SqlTransaction tx, Void argument,
                         final AsyncCallback<Void> callback) {

    this.execute(tx, new ChainedCallback<Integer>(callback) {

      @Override
      public void onSuccess(Integer result) {
        callback.onSuccess(null);
      }
    });

  }

}