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

import com.google.gwt.gears.client.workerpool.WorkerPool;

public class WorkerLogger implements GearsExecutor.Logger {
  private final int ownerWorkerId;
  private final WorkerPool pool;
  private final int executionId;

  public WorkerLogger(WorkerPool pool, int ownerWorkerId, int executionId) {
    this.pool = pool;
    this.ownerWorkerId = ownerWorkerId;
    this.executionId = executionId;
  }

  @Override
  public void log(final String message) {
    if(pool != null) {
      pool.sendMessage(WorkerResponse.newLogResponse(executionId, message), ownerWorkerId);
    }
  }

  @Override
  public void log(String message, Exception e) {
    if(pool != null) {
      pool.sendMessage(WorkerResponse.newLogResponse(executionId, message + e.getMessage()), ownerWorkerId);
    }
  }

  /**
   * Creates a "null" logger, that is a logger that does nothing, to
   * be used in testing.
   * 
   */
  public static GearsExecutor.Logger createNullLogger() {
    return new WorkerLogger(null, 0, 0);
  }

}
