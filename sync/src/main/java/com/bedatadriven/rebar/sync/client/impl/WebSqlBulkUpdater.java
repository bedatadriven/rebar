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

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.websql.WebSqlDatabase;
import com.bedatadriven.rebar.sql.client.websql.WebSqlException;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultCallback;
import com.bedatadriven.rebar.sql.client.websql.WebSqlResultSet;
import com.bedatadriven.rebar.sql.client.websql.WebSqlTransaction;
import com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback;
import com.bedatadriven.rebar.sync.client.BulkUpdaterAsync;
import com.bedatadriven.rebar.sync.client.PreparedStatementBatch;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
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
	
	    database.transaction(new WebSqlTransactionCallback() {
				
			
				@Override
				public void begin(WebSqlTransaction tx) {
			     Log.debug("WebSqlBulkUpdater about to call eval on '" + bulkOperationJsonArray.substring(0, 600) + "'");
			      JsArray<PreparedStatementBatch> statements = PreparedStatementBatch.fromJson(bulkOperationJsonArray);
			      
			      Log.debug("WebSqlBulkUpdater queuing " + statements.length() + " statements");
			      
			      for(int i=0;i!=statements.length();++i) {
			      	PreparedStatementBatch batch = statements.get(i);
				      Log.debug("WebSqlBulkUpdater queuing statement [" + batch.getStatement() + "]");

			        JsArray<JsArrayString> executions = batch.getExecutions();
							if(executions == null || executions.length() == 0) {
			          // simple statement with no parameters
								tx.executeSql(batch.getStatement());
			        
							} else {
								// statement to be executed several times with different parameters
			        	for(int j=0;j!=executions.length(); ++j) {
			        		tx.executeSql(batch.getStatement(), executions.get(j));
			        	}
			        }
			      }
				}
				
				@Override
				public void onSuccess() {
					callback.onSuccess(0);
				}
				
				@Override
				public void onError(WebSqlException e) {
					callback.onFailure(e);
				}
			});
  	} catch(Exception e) {
  		callback.onFailure(e);
  	}
  }
 
}
