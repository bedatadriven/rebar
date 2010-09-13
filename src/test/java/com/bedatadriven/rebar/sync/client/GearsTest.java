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

package com.bedatadriven.rebar.sync.client;

import com.bedatadriven.rebar.sync.client.impl.GearsBulkUpdater;
import com.bedatadriven.rebar.sync.client.impl.GearsExecutor;
import com.bedatadriven.rebar.sync.client.impl.WorkerCommand;
import com.bedatadriven.rebar.sync.client.impl.WorkerLogger;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Date;


/**
 * @author Alex Bertram
 */
public class GearsTest extends GWTTestCase {
  private String dbName;
  private static final String json =
      "[ { statement: \"create table mytest (number int)\" }, "  +
        "{ statement: \"insert into mytest (number) values (?)\", executions: [ [1], [2], [3], [4] ] } " +
      "]";

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.sync.GearsExecutorTest";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();

    // make unique db name to assure we start each test with a clean slate
    dbName = "textExecution" + (new Date()).getTime();
  }

  public void testGearsExecution() throws Exception {
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
    cmd.addOperation(createOp);
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
      GWT.log("number = " + rs.getFieldAsInt(1), null);
      if(i<=numbers.length)
        numbers[i++] = rs.getFieldAsInt(1);
      rs.next();
    }
    assertEquals("count", 3, i);
    assertEquals(3, numbers[0]);
    assertEquals(5, numbers[1]);
    assertEquals(11, numbers[2]);
  }

  public void testGearsExecutorWithJson() throws Exception {
    WorkerCommand cmd = WorkerCommand.newInstance(1);
    cmd.setDatabaseName(dbName);
    cmd.setOperations(json);

    int rowsAffected = GearsExecutor.execute(cmd, WorkerLogger.createNullLogger());

    assertEquals(4, rowsAffected);
    assertSumOfNumbersIsTen();
  }

  private void assertSumOfNumbersIsTen() throws DatabaseException {
    // verify that that the database state is as expected
    Database db = Factory.getInstance().createDatabase();
    db.open(dbName);
    ResultSet rs = db.execute("select sum(number) from mytest");
    assertTrue(rs.isValidRow());
    assertEquals(rs.getFieldAsInt(0), 10);
  }

  public void testGearsWorker() {
    WorkerCommand cmd = WorkerCommand.newInstance(1);
    cmd.setDatabaseName(dbName);
    cmd.setOperations(json);

    GearsBulkUpdater updater = new GearsBulkUpdater();
    updater.executeUpdates(dbName, json, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        throwable.printStackTrace();
        fail(throwable.getMessage());
      }

      @Override
      public void onSuccess(Integer result) {
        assertEquals("rows", 4, (int)result);
        try {
          assertSumOfNumbersIsTen();
        } catch (DatabaseException e) {
          e.printStackTrace();
          fail(e.getMessage());
        }
        finishTest();
      }
    });
    delayTestFinish(1000);
  }

}
