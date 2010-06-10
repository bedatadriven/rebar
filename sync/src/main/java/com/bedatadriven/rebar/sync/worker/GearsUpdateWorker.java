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

package com.bedatadriven.rebar.sync.worker;

import com.bedatadriven.rebar.worker.client.AbstractWorkerEntryPoint;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;

/**
 * @author Alex Bertram
 */
public class GearsUpdateWorker extends AbstractWorkerEntryPoint {

  @Override
  public void onMessageReceived(WorkerPoolMessageHandler.MessageEvent event) {
    WorkerCommand cmd = event.getBodyObject().cast();
    WorkerLogger logger = new WorkerLogger(getPool(), event.getSender(), cmd.getExecutionId());

    try {
      int rowsAffected = GearsExecutor.execute(cmd, logger);
      getPool().sendMessage(WorkerResponse.newSuccessResponse(cmd.getExecutionId(), rowsAffected),
          event.getSender());
    } catch(Exception e) {
      getPool().sendMessage(WorkerResponse.newExceptionResponse(cmd.getExecutionId(), e.getMessage()),
          event.getSender());
    }
  }
}
