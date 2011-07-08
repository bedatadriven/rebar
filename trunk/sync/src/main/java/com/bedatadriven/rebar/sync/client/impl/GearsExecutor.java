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


import com.bedatadriven.rebar.sync.client.PreparedStatementBatch;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

public class GearsExecutor {
  private final WorkerCommand cmd;
  private final Logger logger;
  private Database database;

  public static interface Logger {
    void log(String message);
    void log(String message, Exception e);
  }

  public static int execute(WorkerCommand cmd, Logger logger) throws Exception {
    return new GearsExecutor(cmd, logger).execute();
  }

  private GearsExecutor(WorkerCommand cmd, Logger logger) {
	 
    this.cmd = cmd;
    this.logger = logger;
  }

  private int execute() throws Exception {
    int rowsAffected = 0;

    try {
      openConnection();
      beginTransaction();
      rowsAffected = executeUpdates(cmd.getOperations());
      commitTransaction();
      
    } catch(Exception e) {
    	rollbackSavePoint();
    	throw e;
    } finally {
      closeConnection();
    }
    return rowsAffected;
  }

  private void rollbackSavePoint() throws Exception {
    try {
      database.execute("rollback");
    } catch(Exception e) {
      logger.log(e.getMessage(), e);
    }
  }

  private int executeUpdates(JsArray<PreparedStatementBatch> ops) throws DatabaseException {
    int rowsAffectedCount = 0;
    for(int i = 0; i!= ops.length(); ++i) {
      PreparedStatementBatch stmt = ops.get(i);
      if(stmt.getExecutions() == null || stmt.getExecutions().length()==0) {
        database.execute(stmt.getStatement());
      } else {
        for (int j = 0; j != stmt.getExecutions().length(); ++j) {
          execute(database, stmt.getStatement(), stmt.getExecutions().get(j));
          rowsAffectedCount += database.getRowsAffected();
        }
      }
    }
    return rowsAffectedCount;
  }

  private void beginTransaction() throws DatabaseException {
    database.execute("begin");
  }

  private void commitTransaction() throws DatabaseException {
    database.execute("commit");
  }


  private void openConnection() {
    database = Factory.getInstance().createDatabase();
    database.open(this.cmd.getDatabaseName());
  }

  private void closeConnection() {
    try {
      database.close();
    } catch (DatabaseException e) {
      logger.log("Exception thrown while closing the database: ", e);
    }
  }

  /**
   * Calls the js function directly to avoid converting our js array to java and back again.
   *
   * @param db
   * @param sqlStatement
   * @param args
   * @return
   */
  private native ResultSet execute(Database db, String sqlStatement, JavaScriptObject args) /*-{
        return db.execute(sqlStatement, args);
  }-*/;



}
