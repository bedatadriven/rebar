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

import com.bedatadriven.rebar.persistence.mapping.client.Database;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultCallback;
import com.bedatadriven.rebar.sql.client.websql.WebSqlDatabase;
import com.bedatadriven.rebar.sql.client.websql.WebSqlException;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultSet;
import com.bedatadriven.rebar.sql.client.websql.WebSqlTransaction;
import com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback;
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

   
  	try {
	    WebSqlDatabase database = WebSqlDatabase.openDatabase(databaseName, WebSqlDatabase.ANY_VERSION, databaseName,
	        WebSqlDatabase.DEFAULT_SIZE);
	
	    new Sequence(database, bulkOperationJsonArray, callback);
  	} catch(Exception e) {
  		callback.onFailure(e);
  	}
  }

  private class Sequence {
    private int rowsAffected = 0;
    private JsArray<PreparedStatementBatch> statements;
    private int nextStatementIndex = 0;
    private int nextExecutionIndex = 0;
    private AsyncCallback<Integer> callback;
    private WebSqlTransaction tx;
    private PreparedStatementBatch currentBatch;

    public Sequence(WebSqlDatabase db, String statements, final AsyncCallback<Integer> callback) {
      log("WebSqlBulkUpdater about to call eval on '" + statements + "'");

      this.statements = PreparedStatementBatch.fromJson(statements);
      this.callback = callback;
      log("WebSqlBulkUpdater starting execution of " + this.statements.length() + " statements");

      
      db.transaction(new WebSqlTransactionCallback() {

				@Override
				public void begin(WebSqlTransaction tx) {
					Sequence.this.tx = tx;
		      executeNextStatement();
				}
      	
				@Override
				public void onSuccess() {
					callback.onSuccess(rowsAffected);
				}
				
				@Override
				public void onError(WebSqlException e) {
					callback.onFailure(e);
				}
				

			});
    }

    public void executeNextStatement() {
      log("WebSqlBulkUpdater: executing statement " + nextStatementIndex);

      currentBatch = statements.get(nextStatementIndex++);
      if(currentBatch.getExecutions() == null || currentBatch.getExecutions().length() == 0) {
        executeStatementWithoutParams(currentBatch);
      } else {
        nextExecutionIndex = 0;
        executeNextExecution();
      }
    }

    private void executeStatementWithoutParams(PreparedStatementBatch batch) {
      log("WebSqlBulkUpdater: executeStatementWithoutParams");
      
      tx.executeSql(batch.getStatement(), JsArray.createArray(), new WebSqlResultCallback() {
        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
          rowsAffected = results.getRowsAffected();
          onBatchFinished();
        }

        @Override
        public void onFailure(WebSqlException e) {
          callback.onFailure(e);
        }
      });
    }

    private void executeNextExecution() {
      
      log("WebSqlBulkUpdater: executeNextExecution  = " + nextExecutionIndex);

      tx.executeSql(currentBatch.getStatement(), currentBatch.getExecutions().get(nextExecutionIndex++),
          new WebSqlResultCallback() {
        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
          log("executeSql succeeded, " + results.getRowsAffected() + " rows affected.");
          rowsAffected = results.getRowsAffected();
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
  
  private static native final void log(String message) /*-{
      console.log(message);
  }-*/;
  
}
