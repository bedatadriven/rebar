package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.bulk.PreparedStatementBatch;
import com.bedatadriven.rebar.sql.client.query.SqlDialect;
import com.bedatadriven.rebar.sql.client.query.SqliteDialect;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

class SqlDatabaseImpl extends SqlDatabase {

  private static Logger LOGGER = java.util.logging.Logger.getLogger(SqlDatabaseImpl.class.getName());

  private final String name;
  private final WebSqlDatabase db;

  public SqlDatabaseImpl(String name) {
    super();
    this.name = name;
    this.db = WebSqlDatabase.openDatabase(name, WebSqlDatabase.ANY_VERSION, name, WebSqlDatabase.DEFAULT_SIZE);
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public SqlDialect getDialect() {
    return SqliteDialect.INSTANCE;
  }


  /**
   * Begins an asynchronous transaction using the common SQL API
   *
   * @param callback
   */
  @Override
  public void transaction(final SqlTransactionCallback callback) {
    db.transaction(new WebSqlTransactionCallback() {

      @Override
      public void onError(WebSqlException e) {
        LOGGER.severe("WebSql tx failed");
        callback.onError(e);
      }

      @Override
      public void begin(WebSqlTransaction tx) {
        callback.begin(tx);
      }

      @Override
      public void onSuccess() {
        LOGGER.finest("WebSql tx succeeded");
        callback.onSuccess();
      }
    });
  }

  @Override
  public void executeUpdates(final String bulkOperationJsonArray,
                             final AsyncCallback<Integer> callback) {


    try {

      final Counter counter = new Counter();

      db.transaction(new WebSqlTransactionCallback() {


        @Override
        public void begin(WebSqlTransaction tx) {
          LOGGER.fine("WebSqlBulkUpdater about to call eval on '" + shortenJson(bulkOperationJsonArray) + "'");
          JsArray<PreparedStatementBatch> statements = PreparedStatementBatch.fromJson(bulkOperationJsonArray);

          LOGGER.fine("WebSqlBulkUpdater queuing " + statements.length() + " statements");

          for (int i = 0; i != statements.length(); ++i) {
            PreparedStatementBatch batch = statements.get(i);
            LOGGER.fine("WebSqlBulkUpdater queuing statement [" + batch.getStatement() + "]");

            if (GWT.isScript()) {
              doBatch(counter, tx, batch);
            } else {
              try {
                doBatch(counter, tx, batch);
              } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Hosted mode exception, retrying...", e);
                doBatch(counter, tx, batch);
              }
            }
          }
        }

        private void doBatch(final Counter counter,
                             WebSqlTransaction tx, PreparedStatementBatch batch) {
          JsArray<JsArrayString> executions = batch.getExecutions();
          if (executions == null || executions.length() == 0) {
            // simple statement with no parameters
            tx.executeSql(batch.getStatement(), counter);

          } else {
            // statement to be executed several times with different parameters
            for (int j = 0; j != executions.length(); ++j) {
              tx.executeSql(batch.getStatement(), executions.get(j), counter);
            }
          }
        }

        @Override
        public void onSuccess() {
          callback.onSuccess(counter.getRowsAffected());
        }

        @Override
        public void onError(WebSqlException e) {
          callback.onFailure(e);
        }
      });
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  private String shortenJson(final String json) {
    if (json.length() <= 600) {
      return json;
    } else {
      return json.substring(0, 550) + "...";
    }
  }

}
