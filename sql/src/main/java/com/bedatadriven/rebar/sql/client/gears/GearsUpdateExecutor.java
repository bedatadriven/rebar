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


import com.bedatadriven.rebar.sql.client.bulk.PreparedStatementBatch;
import com.bedatadriven.rebar.sql.client.gears.worker.WorkerCommand;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

public class GearsUpdateExecutor {
	
	/**
	 * maximum time to wait for a lock
	 */
  private static final int MAX_WAIT_MS = 120 * 1000;
  
	private final WorkerCommand cmd;
  private final Logger logger;
  private Database database;

  public static interface Logger {
    void log(String message);
    void log(String message, Exception e);
  }

  public static int execute(WorkerCommand cmd, Logger logger) throws Exception {   
		return new GearsUpdateExecutor(cmd, logger).execute();
  }

  private GearsUpdateExecutor(WorkerCommand cmd, Logger logger) {
    this.cmd = cmd;
    this.logger = logger;
  }

  private int execute() throws Exception {
    int rowsAffected = 0;

    try {
    	
      JsArray<PreparedStatementBatch> operations = cmd.getOperations();
      
    	beginTransaction();
			rowsAffected = executeUpdates(operations);
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

  private void beginTransaction() throws Exception {
  	double startTime = now();
  	
  	//logger.log("Starting attempt to obtain lock...");
  	
  	// the database may be locked by the main thread.
  	// keep retrying until we get a lock
  	while(true) {
	  	try {
	  		// by including 'EXCLUSIVE' we assure that the transaction begins 
	  		// immediately rather than waiting for the first non-select statement.
	  		// With 'EXCLUSIVE' we don't risk getting a locked exception on subsequent 
	  		// commands
	      database = Factory.getInstance().createDatabase();
	      database.open(this.cmd.getDatabaseName());
	  		database.execute("BEGIN EXCLUSIVE TRANSACTION");
	  		return;
	  		
	  	} catch(Exception e) {
	  		if(database != null) {
	  			try {
	  				database.close();
	  			} catch(Exception ignored) {
	  			}
	  		}
	  		if(now() > startTime + MAX_WAIT_MS) {
	  			throw new RuntimeException("Failed to obtain a lock after wating for " + MAX_WAIT_MS + " ms", e);
	  		}
	  		logger.log("Database locked, retrying...");
	  	}
  	}
  }

  private void commitTransaction() throws DatabaseException {
    database.execute("commit");
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
  
  private static native double now() /*-{
  	var d = new Date();
  	return d.getMilliseconds();
  }-*/;
}
