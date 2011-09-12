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

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.gears.GearsUpdateExecutor;
import com.bedatadriven.rebar.sql.client.gears.worker.WorkerCommand;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.junit.client.GWTTestCase;
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

  public void testGearsExecutor() throws Exception {
  
  	Log.debug("================= testGearsExecutor == gears_test ========");

  	if(Factory.getInstance() == null) {
  		Log.debug("Not a gears platform, skipping.");
  		return;
  	}
  	
  	String json = WorkerCommand.newCommandAsJson(1, "gears_test", JSON_UPDATES);
  	
  	GearsUpdateExecutor.Logger logger = new GearsUpdateExecutor.Logger() {
			
			@Override
			public void log(String message, Exception e) {
				Log.debug(message, e);
			}
			
			@Override
			public void log(String message) {
				Log.debug(message);
			}
		}; 
		
		Log.debug("json = " + json);
  	
  	WorkerCommand cmd = WorkerCommand.fromJson(json);
  	Log.debug("json parsed");
  	
		int rowsAffected = GearsUpdateExecutor.execute(cmd, logger);
  	
  	assertEquals(8, rowsAffected);
  	
  }
  
  
  public void testBasic() {
  	Log.debug("================= testBasic == db1 ========");

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

    delayTestFinish(30000);
  }
  

  public void testSingle() {
  	Log.debug("================= testSingle == db2 ========");

    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
    SqlDatabase db = factory.open("db2");
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists numbers (x INT)");
        tx.executeSql("insert into numbers (x) values (33) ");
        tx.executeSql("insert into numbers (x) values (41) ");
        tx.executeSql("insert into numbers (x) values (?) ", new Object[] {null});
        tx.executeSql("insert into numbers (x) values (?) ", new Object[] {true});
        tx.executeSql("select sum(x) from numbers",  new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertEquals(75, (int)results.intResult());
          }
        });
        tx.executeSql("select count(*) from numbers where x is null", new SqlResultCallback() {

					@Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						assertEquals(1, (int)results.intResult());
          }
        });
      }

			@Override
      public void onSuccess() {
				finishTest();
      }

			@Override
      public void onError(SqlException e) {
        fail(e.getMessage());
      }
    });

    delayTestFinish(2000);
  }
  
  public void testExecutorWithJson() throws Exception {

  	Log.debug("================= testExecutorWithJson == db4 ========");

  	
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
        assertEquals("testExecutorWithJson: rows affected", 8, (int)rowsAffected);
        finishTest();
      }
    });

    delayTestFinish(30000);
  }

  public void testExecutorWithJsonAndLocking() throws Exception {

  	Log.debug("================= testExecutorWithJsonAndLocking == db5 ========");
  	
    SqlDatabaseFactory factory = GWT.create(SqlDatabaseFactory.class);
    final SqlDatabase db = factory.open("db5"); 
    db.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {

				// we've know got a lock on the main js event loop for this database
				
				tx.executeSql("create table nonsense (id INTEGER)");
				tx.executeSql("select * from sqlite_master", new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {

						// this should queue a transaction on the worker 
						db.executeUpdates(JSON_UPDATES, new AsyncCallback<Integer>() {
							@Override
							public void onFailure(Throwable throwable) {
								Window.alert("failed: " + throwable.getMessage());
								fail(throwable.getMessage());
							}

							@Override
							public void onSuccess(Integer rowsAffected) {
								assertEquals("testExecutorWithJsonAndLocking: rows affected", 8, (int)rowsAffected);
								finishTest();
							}
						});	
					}
				});
				
				// spend more time in the main event loop, keeping the transaction alive
				// and the lock active. we want to assure that even if the worker thread times out 
				// waiting for a lock, the 
				Log.debug("starting busy work");
				for(int i=0;i!=5000;++i) {
					tx.executeSql("insert into nonsense (id) values (?)", new Object[] { i });
				}
			}
			
			@Override
      public void onSuccess() {
				Log.debug("transaction commited on the main event loop");
      }


			@Override
      public void onError(SqlException e) {
	      fail(e.getMessage());
      }
		});
   
    delayTestFinish(30000);
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
