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

package com.bedatadriven.rebar.sql.client;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Integration test case that verifies that our emulation classes,
 * gears jdbc implementation, and gears/gwt functions correctly together.
 */
public class SqlTest extends GWTTestCase {

  private static final String JSON_UPDATES  =
      "[ { statement: \"create table mytest (number int)\" }, "  +
        "{ statement: \"delete from mytest where number = 99\" }, " +
        "{ statement: \"insert into mytest (number) values (?)\", executions: [ [1], [2], [3], [4] ] }, " + 
        "{ statement: \"insert into mytest (number) values (?)\", executions: [ [91], [92], [93], [94] ] } " +
      "]";

	
  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "com.bedatadriven.rebar.sql.SqlTest";
  }
  
  private int callbacks = 0;

  public void testBasic() {

    final List<SqlResultSet> list = new ArrayList<SqlResultSet>();

    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
    SqlDatabase db = factory.open("db1");
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists foobar (id INT, name TEXT)");
        tx.executeSql("insert into foobar (id, name) values (1, 'foo') ");
        tx.executeSql("insert into foobar (id, name) values (2, 'bar') ");
        tx.executeSql("select * from foobar where id > ?", new Object[] { 1 }, new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertEquals(1, results.getRows().size());
            assertEquals("bar", results.getRow(0).getString("name"));

            callbacks ++;
          }
        });

        // try without params
        tx.executeSql("select * from foobar", new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertEquals(2, results.getRows().size());
            assertEquals(1, callbacks);
            
            callbacks ++;
          }
        });
      }

			@Override
      public void onError(SqlException e) {
        fail(e.getMessage());
      }
			
      @Override
      public void onSuccess() {
      	assertEquals(2, callbacks);
        finishTest();
      }
    });

    delayTestFinish(2000);
  }
  

  public void testSingle() {

    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
    SqlDatabase db = factory.open("db2");
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists numbers (x INT)");
        tx.executeSql("insert into numbers (x) values (33) ");
        tx.executeSql("insert into numbers (x) values (41) ");
        tx.executeSql("select sum(x) from numbers",  new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertEquals(74, (int)results.intResult());
            finishTest();
          }
        });
      }

			@Override
      public void onError(SqlException e) {
        fail(e.getMessage());
      }
    });

    delayTestFinish(2000);
  }
  

  public void testExecutorWithJson() throws Exception {

    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
    SqlDatabase db = factory.open("db4");    
    db.executeUpdates(JSON_UPDATES, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        Window.alert("failed: " + throwable.getMessage());
        fail(throwable.getMessage());
      }

      @Override
      public void onSuccess(Integer rowsAffected) {
        assertEquals("rows affected", 8, (int)rowsAffected);
        finishTest();
      }
    });

    delayTestFinish(1000);
  }

  
  
//  public void testDates() {
//
//    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
//    SqlDatabase db = factory.open("dates");
//    db.transaction(new SqlTransactionCallback() {
//      @Override
//      public void begin(SqlTransaction tx) {
//        tx.executeSql("create table if not exists timestamps (t TEXT)");
//        tx.executeSql("insert into timestamps (x) values (33) ");
//        tx.executeSql("insert into numbers (x) values (41) ");
//        tx.executeSql("select sum(x) from numbers",  new SqlResultCallback() {
//          @Override
//          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
//            assertEquals(74, (int)results.intResult());
//            finishTest();
//          }
//        });
//      }
//
//			@Override
//      public void onError(SqlException e) {
//        fail(e.getMessage());
//      }
//    });
//
//    delayTestFinish(2000);
//  }


}
