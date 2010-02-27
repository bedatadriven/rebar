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

import com.bedatadriven.rebar.sql.async.worker.GearsExecutor;
import com.bedatadriven.rebar.sql.async.worker.WorkerCommand;
import com.bedatadriven.rebar.sql.async.worker.WorkerLogger;
import com.bedatadriven.rebar.sql.async.worker.WorkerResponse;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;


/**
 * @author Alex Bertram
 */
public class GearsExecutorTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.sql.async.GearsExecutorTest";
  }

  public void testExecution() throws Exception {

    // make unique db name to assure we start each test with a clean slate
    String dbName = "textExecution" + (new Date()).getTime();

    PreparedStatementBatch createOp = PreparedStatementBatch.newInstance();
    createOp.setStatement("create table prime_numbers (name text, number int, date_discovered int)");

    PreparedStatementBatch insertOp = PreparedStatementBatch.newInstance();
    insertOp.setStatement("insert into prime_numbers (name, number, date_discovered) values ( ?, ?, ? )");
    insertOp.addExecution("Three", 3, new Date());
    insertOp.addExecution("Five", 5, new Date());
    insertOp.addExecution("Nine", 9, new Date());
    insertOp.addExecution("Eleven", 11, new Date());

    PreparedStatementBatch deleteOp = PreparedStatementBatch.newInstance();
    deleteOp.setStatement("delete from prime_numbers where number = ?");
    deleteOp.addExecution(9);

    WorkerCommand cmd = WorkerCommand.newInstance(1);
    cmd.setDatabaseName(dbName);
    cmd.addOperation(insertOp);
    cmd.addOperation(deleteOp);

    int rowsAffected = GearsExecutor.execute(cmd, WorkerLogger.createNullLogger());

    // verify that that the database state is as expected
    Database db = Factory.getInstance().createDatabase();
    db.open(dbName);
    ResultSet rs = db.execute("select name, number, date_discovered from prime_numbers order by number");
    int[] numbers = new int[3];
    int i=0;
    while(rs.isValidRow()) {
      numbers[i++] = rs.getFieldAsInt(1);
      rs.next();
    }
    assertEquals("count", 3, i);
    assertEquals(4, numbers[0]);
    assertEquals(5, numbers[1]);
    assertEquals(11, numbers[2]);
  }
}
