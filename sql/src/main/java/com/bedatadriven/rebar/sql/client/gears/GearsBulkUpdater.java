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

package com.bedatadriven.rebar.sql.client.gears;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bedatadriven.rebar.sql.client.gears.worker.WorkerCommand;
import com.bedatadriven.rebar.sql.client.gears.worker.WorkerResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.workerpool.WorkerPool;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * Executes a PreparedStatementBatch asynchronously, using a gears worker.
 *
 * @author Alex Bertram
 */
class GearsBulkUpdater implements WorkerPoolMessageHandler {

	public static final GearsBulkUpdater INSTANCE = new GearsBulkUpdater();

	private static final Logger LOGGER = Logger.getLogger(GearsBulkUpdater.class.getName());
	
	private WorkerPool pool;
	private int workerId = -1;
	private int nextExecutionId = 1;
	private Map<Integer, AsyncCallback> callbacks = new HashMap<Integer, AsyncCallback>();

	private GearsBulkUpdater() {

	}

	public void executeUpdates(String databaseName, String bulkOperationJsonArray, AsyncCallback<Integer> callback) {
		try {
			int executionId = nextExecutionId++;
			LOGGER.finer("GearsBulkUpdater: starting executeUpdates() for executionId=" + executionId);

			// Create our worker if we haven't already
			if(pool == null) {
				initPool();
			}

			// Construct our message
			LOGGER.finer("GearsBulkUpdater: constructing message");

			String command = WorkerCommand.newCommandAsJson(executionId, databaseName, bulkOperationJsonArray);

			// Register our callback
			callbacks.put(executionId, callback);

			// Dispatch our command to the worker
			try {
				sendMessage(command);    
			} catch(Throwable t) {
				// this is failing inexplicably in IE on second attempts to synchronize,
				// let's try reinitializng the pool and reloading the worker
				initPool();
				sendMessage(command);
			}
			
			LOGGER.finer("GearsBulkUpdater: sent message to worker");
		} catch(Throwable e) {
			LOGGER.log(Level.WARNING, "GearsBulkUpdater: exception thrown while sending message: " + e.getMessage(), e);
			callback.onFailure(e);
		}
	}

	private void sendMessage(String command) {
	  LOGGER.finer("GearsBulkUpdater: about to send message to worker " + workerId);
	  sendMessageSafe( pool, command, workerId );
  }

	private void initPool() {
	  pool = Factory.getInstance().createWorkerPool();
	  pool.setMessageHandler(this);
	  workerId = pool.createWorkerFromUrl(GWT.getModuleBaseURL() +
	  		"GearsSqlWorker.js");

	  LOGGER.fine("GearsBulkUpdater: Created worker pool, workerId = " + workerId);
  }

	public void onMessageReceived(MessageEvent messageEvent) {

		WorkerResponse response;
		try {
			response =  WorkerResponse.parse( messageEvent.getBody() );
		} catch(Throwable t) {
			LOGGER.fine("GearsBulkUpdater: exception parsing worker response. response = " + messageEvent.getBody()
					+ ", exception = " + t.getMessage());
			return;
		}

		if(response.getType() == WorkerResponse.LOG) {
			// Log message from Worker
			LOGGER.info("WorkerBulkExecutor[" + response.getExecutionId() + "] : " + response.getMessage());

		} else {
			// Find the callback for this execution
			AsyncCallback<Integer> callback = callbacks.get(response.getExecutionId());

			if(response.getType() == WorkerResponse.EXCEPTION) {
				LOGGER.log(Level.SEVERE, "GearsBulkUpdater[" + response.getExecutionId() + 
						"] : Exception thrown during execution: " + response.getMessage());
				callback.onFailure(new Exception(response.getMessage()));
				callbacks.remove(response.getExecutionId());

			} else if(response.getType() == WorkerResponse.SUCCESS) {
				LOGGER.fine("GearsBulkUpdater[" + response.getExecutionId() + "] : Completed successfully: " +
						response.getRowsAffected() + " row(s) affected");

				callback.onSuccess(response.getRowsAffected());
				callbacks.remove(response.getExecutionId());

			} else {
				LOGGER.severe("WorkerBulkExecutor: Unknown response type = " + response.getType());
			}
		}
	}

	private static native void sendMessageSafe(WorkerPool pool, String message, int workerId) /*-{
		pool.sendMessage(String(message), Number(workerId));
	}-*/;
}
