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

package com.bedatadriven.rebar.sql.async.client;

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.async.worker.WorkerCommand;
import com.bedatadriven.rebar.sql.async.worker.WorkerResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.workerpool.WorkerPool;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Executes a PreparedStatementBatch asynchronously, using a web worker.
 *
 * @author Alex Bertram
 */
public class GearsAsyncConnection implements AsyncConnection, WorkerPoolMessageHandler {

  private String databaseName;
  private WorkerPool pool;
  private Integer workerId;
  private int nextExecutionId = 1;
  private Map<Integer, AsyncCallback> callbacks = new HashMap<Integer, AsyncCallback>();

  public GearsAsyncConnection(String databaseName) {
    this.databaseName = databaseName;
  }

  public void executeUpdates(String bulkOperationJsonArray, AsyncCallback<Integer> callback) {

    // Create our worker if we haven't already
    if(pool == null) {
      pool = Factory.getInstance().createWorkerPool();
      pool.setMessageHandler(this);
      workerId = pool.createWorkerFromUrl(GWT.getModuleBaseURL() +
          "GearsSqlAsyncWorker/GearsSqlAsyncWorker.js");
    }

    // Construct our message
    WorkerCommand cmd = WorkerCommand.newInstance(nextExecutionId++);
    cmd.setDatabaseName(databaseName);
    cmd.setOperations(bulkOperationJsonArray);

    // Register our callback
    callbacks.put(cmd.getExecutionId(), callback);

    // Dispatch our command to the worker
    pool.sendMessage(cmd, workerId);
  }

  public void onMessageReceived(MessageEvent messageEvent) {
    WorkerResponse response = messageEvent.getBodyObject().cast();

    if(response.getType() == WorkerResponse.LOG) {
      // Log message from Worker
      Log.debug("WorkerBulkExecutor[" + response.getExecutionId() + "] : " + response.getMessage());

    } else {
      // Find the callback for this execution
      AsyncCallback<Integer> callback = callbacks.get(response.getExecutionId());

      if(response.getType() == WorkerResponse.EXCEPTION) {
        Log.error("WorkerBulkExecutor[" + response.getExecutionId() + "] : Exception thrown during execution: " + response.getMessage());
        callback.onFailure(new Exception(response.getMessage()));
        callbacks.remove(response.getExecutionId());

      } else if(response.getType() == WorkerResponse.SUCCESS) {
        Log.debug("WorkerBulkExecutor[" + response.getExecutionId() + "] : Completed successfully: " +
            response.getRowsAffected() + " row(s) affected");

        callback.onSuccess(response.getRowsAffected());
        callbacks.remove(response.getExecutionId());

      } else {
        Log.error("WorkerBulkExecutor: Unknown response type = " + response.getType());
      }
    }
  }
}
