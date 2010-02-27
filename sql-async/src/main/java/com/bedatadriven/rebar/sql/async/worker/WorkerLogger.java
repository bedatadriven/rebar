/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.sql.async.worker;

import com.google.gwt.gears.client.workerpool.WorkerPool;

public class WorkerLogger {
  private final int ownerWorkerId;
  private final WorkerPool pool;
  private final int executionId;

  public WorkerLogger(WorkerPool pool, int ownerWorkerId, int executionId) {
    this.pool = pool;
    this.ownerWorkerId = ownerWorkerId;
    this.executionId = executionId;
  }

  public void log(final String message) {
    if(pool != null) {
      pool.sendMessage(WorkerResponse.newLogResponse(executionId, message), ownerWorkerId);
    }
  }

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
  public static WorkerLogger createNullLogger() {
    return new WorkerLogger(null, 0, 0);
  }

}
