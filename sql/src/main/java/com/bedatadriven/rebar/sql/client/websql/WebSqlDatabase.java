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

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.core.client.JavaScriptObject;

public final class WebSqlDatabase extends JavaScriptObject implements SqlDatabase {



  protected WebSqlDatabase() {
  }

  /**
   *
   * @param name
   * @param version
   * @param displayName
   * @param estimatedSize estimated size — in bytes — of the data that will be stored in the database
   * @param creationCallback  a callback to be invoked if the database has not yet been created.
   */
  public static native WebSqlDatabase openDatabase(String name, double version, String displayName, int estimatedSize,
                      CreationCallback creationCallback) /*-{
    return $wnd.openDatabase(name, version, displayName, estimatedSize,
        function(db) {
          creationCallback.@com.bedatadriven.rebar.sql.client.websql.CreationCallback::onCreated(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlDatabase;)(db);
        }
     );
  }-*/;

  /**
   *
   * @param name
   * @param version
   * @param displayName
   * @param estimatedSize estimated size — in bytes — of the data that will be stored in the database
   */
  public static native WebSqlDatabase openDatabase(String name, double version, String displayName, int estimatedSize) /*-{
    return $wnd.openDatabase(name, version, displayName, estimatedSize);
  }-*/;

  public native SqlTransaction transaction(WebSqlTransactionCallback callback) /*-{
    this.transaction(function(tx) {
      callback.@com.bedatadriven.rebar.sql.client.websql.TransactionCallback::begin(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlTransaction;)(tx);
    }, function(e) {
      callback.@com.bedatadriven.rebar.sql.client.websql.TransactionCallback::onError(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlException;)(
          @com.bedatadriven.rebar.sql.client.websql.WebSqlException::new(Ljava/lang/String;I)(e.message,e.code));
    });
  }-*/;

  @Override
  public void transaction(final SqlTransactionCallback callback) {
    transaction(new SqlTransactionCallback() {
      
      @Override
      public void onError(SqlException e) {
        callback.onError(e);
      }
      
      @Override
      public void begin(SqlTransaction tx) {
        callback.begin(tx);        
      }
    });
  }
  

}