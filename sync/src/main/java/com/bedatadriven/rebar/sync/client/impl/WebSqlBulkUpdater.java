/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.bedatadriven.rebar.sync.client.impl;

import com.bedatadriven.rebar.sql.client.websql.*;
import com.bedatadriven.rebar.sync.client.BulkUpdaterAsync;
import com.bedatadriven.rebar.sync.client.PreparedStatementBatch;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Does bulk updating using the Web SQL API
 */
public class WebSqlBulkUpdater implements BulkUpdaterAsync {

  @Override
  public void executeUpdates(String databaseName, final String bulkOperationJsonArray,
                             final AsyncCallback<Integer> callback) {

    WebSqlDatabase database = WebSqlDatabase.openDatabase(databaseName, 1, databaseName,
        1024 * 1024 * 5);

    database.transaction(new TransactionCallback() {
      @Override
      public void begin(WebSqlTransaction tx) {
        new Sequence(bulkOperationJsonArray, callback);
      }

      @Override
      public void onError(WebSqlException e) {
        callback.onFailure(e);
      }
    });

  }

  private class Sequence {
    private int rowsAffected = 0;
    private JsArray<PreparedStatementBatch> statements;
    private int nextStatementIndex = 0;
    private int nextExecutionIndex = 0;
    private AsyncCallback<Integer> callback;
    private WebSqlTransaction tx;
    private PreparedStatementBatch currentBatch;

    public Sequence(String statements, AsyncCallback<Integer> callback) {
      this.statements = PreparedStatementBatch.fromJson(statements);
      this.callback = callback;
    }

    public void executeNextStatement() {
      currentBatch = statements.get(nextStatementIndex++);
      if(currentBatch.getExecutions() == null || currentBatch.getExecutions().length() == 0) {
        executeStatementWithoutParams(currentBatch);
      } else {
        nextExecutionIndex = 0;
        executeNextExecution();
      }
    }

    private void executeStatementWithoutParams(PreparedStatementBatch batch) {
      tx.executeSql(batch.getStatement(), JsArray.createArray(), new ResultCallback() {
        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
          rowsAffected += results.getRowsAffected();
          onBatchFinished();
        }

        @Override
        public void onFailure(WebSqlException e) {
          callback.onFailure(e);
        }
      });
    }

    private void executeNextExecution() {
      tx.executeSql(currentBatch.getStatement(), currentBatch.getExecutions().get(nextExecutionIndex++),
          new ResultCallback() {
        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
          rowsAffected += results.getRowsAffected();
          if(nextExecutionIndex < currentBatch.getExecutions().length()) {
            executeNextExecution();
          } else {
            onBatchFinished();
          }
        }

        @Override
        public void onFailure(WebSqlException e) {
          callback.onFailure(e);
        }
      });
    }

    private void onBatchFinished() {
      if(nextStatementIndex < statements.length()) {
        executeNextStatement();
      } else {
        callback.onSuccess(rowsAffected);
      }
    }
  }
}
