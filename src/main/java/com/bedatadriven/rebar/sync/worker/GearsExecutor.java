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

package com.bedatadriven.rebar.sync.worker;

import com.bedatadriven.rebar.sync.client.PreparedStatementBatch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

public class GearsExecutor {
  private final WorkerCommand cmd;
  private final WorkerLogger logger;
  private Database database;

  public static int execute(WorkerCommand cmd, WorkerLogger logger) throws Exception {
    return new GearsExecutor(cmd, logger).execute();
  }

  private GearsExecutor(WorkerCommand cmd, WorkerLogger logger) {
    this.cmd = cmd;
    this.logger = logger;

  }

  private int execute() throws Exception {
    try {
      openConnection();
      beginTransaction();
      int rowsAffected = executeUpdates(cmd.getOperations());
      commitTransaction();
      return rowsAffected;
    } catch(Exception e) {
      rollbackSavePoint();
      throw e;
    } finally {
      closeConnection();
    }
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
