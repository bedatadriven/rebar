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

import com.google.gwt.junit.client.GWTTestCase;

public class WebSqlTest extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "com.bedatadriven.rebar.sql.SqlTest";
  }

  public void testInsert() {

    WebSqlDatabase db = WebSqlDatabase.openDatabase("testInsert", 1, "My test database", 1024 * 50);
    assertNotNull(db);
    db.transaction(new TransactionCallback() {
      @Override
      public void begin(WebSqlTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS MyTable (id unique, text) ");
        for (int i = 0; i != 100; ++i) {
          tx.executeSql("INSERT INTO MyTable (id, text) VALUES (?, ?) ",
              new Object[]{i, i + " balloons"});
        }
        finishTest();
      }

      @Override
      public void onError(WebSqlException e) {
        fail(e.getMessage());
      }
    });
    delayTestFinish(10000);
  }

  public void testInsertAndSelect() {

    WebSqlDatabase db = WebSqlDatabase.openDatabase("insertAndSelect", 1, "My test database", 1024 * 50);
    assertNotNull(db);
    db.transaction(new TransactionCallback() {
      @Override
      public void begin(WebSqlTransaction tx) {
        tx.executeSql("CREATE TABLE IF NOT EXISTS MyTable (id unique, text) ");
        tx.executeSql("INSERT INTO MyTable (id, text) VALUES (?, ?) ",
            new Object[] { 1, "balloons" });
        tx.executeSql("SELECT id, text FROM MyTable", new ResultCallback() {
          @Override
          public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
            assertEquals(1, results.getRows().length());
            finishTest();
          }

          @Override
          public void onFailure(WebSqlException e) {
            fail(e.getMessage() + ", Code = " + e.getCode());
          }
        });
      }

      @Override
      public void onError(WebSqlException e) {
        fail(e.getMessage());
      }
    });
    delayTestFinish(100000);
  }
}
