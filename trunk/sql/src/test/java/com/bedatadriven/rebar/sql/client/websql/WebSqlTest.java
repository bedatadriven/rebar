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

package com.bedatadriven.rebar.sql.client.websql;

import java.util.Date;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.junit.client.GWTTestCase;

public class WebSqlTest extends GWTTestCase {

  private SqlDatabase db;

	
  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "com.bedatadriven.rebar.sql.SqlTest";
  }
  
  

  @Override
  protected void gwtSetUp() throws Exception {
    db = new WebSqlDatabaseFactory().open("test" + new Date().getTime());
  }



  public void testInsert() {

    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS MyTable (id unique, text) ");
        for (int i = 0; i != 100; ++i) {
          tx.executeSql("INSERT INTO MyTable (id, text) VALUES (?, ?) ",
              new Object[]{i, i + " balloons"});
        }
        finishTest();
      }

      @Override
      public void onError(SqlException e) {
        fail(e.getMessage());
      }
    });
    delayTestFinish(10000);
  }

  public void testInsertAndSelect() {

    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS MyTable (id unique, text, count) ");
        tx.executeSql("INSERT INTO MyTable (id, text, count) VALUES (?, ?, ?) ",
            new Object[] { 1, "balloons", 99 });
        tx.executeSql("INSERT INTO MyTable (id, text, count) VALUES (?, ?, ?) ",
            new Object[] { 2, "plates", 3.14 });
        tx.executeSql("INSERT INTO MyTable (id, text) VALUES (?, ?) ",
            new Object[] { 3, "gizmos" });
        tx.executeSql("INSERT INTO MyTable (id, text, count) VALUES (?, ?, ?) ",
            new Object[] { 4, "gizmos squared", null });
        tx.executeSql("SELECT id, text, count FROM MyTable ORDER BY id", new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertEquals(4, results.getRows().length());
            assertEquals("balloons", results.getRows().getRow(0).getString("text"));
            assertEquals(99, results.getRows().getRow(0).getInt("count"));
            assertEquals(3.14, results.getRows().getRow(1).getDouble("count"));
            assertFalse("rows[1].count is null", results.getRows().getRow(1).isNull("count"));
            assertTrue("rows[2].count is null", results.getRows().getRow(2).isNull("count"));
            assertTrue("rows[3].count is null", results.getRows().getRow(3).isNull("count"));
            assertFalse("rows[3].text is null", results.getRows().getRow(3).isNull("text"));

            finishTest();
          }

          @Override
          public boolean onFailure(SqlException e) {
            fail(e.getMessage());
            return false;
          }
        });
      }

      @Override
      public void onError(SqlException e) {
        fail(e.getMessage());
      }
    });
    delayTestFinish(100000);
  }
  
  
  

}
