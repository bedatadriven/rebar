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

package com.bedatadriven.rebar.sql.worker.client;

import com.bedatadriven.rebar.sql.client.gears.GearsUpdateExecutor;
import com.bedatadriven.rebar.sql.client.gears.worker.WorkerCommand;
import com.bedatadriven.rebar.sql.client.gears.worker.WorkerResponse;
import com.bedatadriven.rebar.worker.client.AbstractWorkerEntryPoint;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;

/**
 * @author Alex Bertram
 */
public class GearsSqlWorker extends AbstractWorkerEntryPoint {

  @Override
  public void onMessageReceived(WorkerPoolMessageHandler.MessageEvent event) {
    WorkerCommand cmd = WorkerCommand.fromJson(event.getBody());
    
    GearsUpdateExecutor.Logger logger = new WorkerLogger(getPool(), event.getSender(), cmd.getExecutionId());
    logger.log("GearSqlWorker received message");
    try {
      int rowsAffected = GearsUpdateExecutor.execute(cmd, logger);
      getPool().sendMessage(WorkerResponse.newSuccessResponse(cmd.getExecutionId(), rowsAffected),
          event.getSender());
    } catch(Exception e) {
      getPool().sendMessage(WorkerResponse.newExceptionResponse(cmd.getExecutionId(), e.getMessage()),
          event.getSender());
    }
  }
}
