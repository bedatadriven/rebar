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
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript Overlay of the WebSql API Database object.
 * <p/>
 * This wrappers also implements the common {@link SqlDatabase} interface.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#databases for definitions.">W3 standard</a>
 */
public final class WebSqlDatabase extends JavaScriptObject {


  public static final String ANY_VERSION = "";

  public static final int DEFAULT_SIZE = 1024 * 1024 * 4;

  protected WebSqlDatabase() {
  }

  /**
   * WebSql-specific method to open a client-side database.
   *
   * @param name             the name of the database.
   * @param version          the expected version of the database, or an empty string if any version is acceptable. If a
   *                         database with the same name but different version already exists, then an {@code INVALID_STATE_ERR}
   * @param displayName      the name to present to the user
   * @param estimatedSize    estimated size — in bytes — of the data that will be stored in the database
   * @param creationCallback a callback to be invoked if the database has not yet been created.
   * @see <a href="http://www.w3.org/TR/webdatabase/#dom-opendatabase">W3 Standard</a>
   */
  public static native WebSqlDatabase openDatabase(String name, String version, String displayName, int estimatedSize,
                                                   WebSqlCreationCallback creationCallback) /*-{
    return $wnd.openDatabase(name, version, displayName, estimatedSize,
        function(db) {
          creationCallback.@com.bedatadriven.rebar.sql.client.websql.WebSqlCreationCallback::onCreated(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlDatabase;)(db);
        }
     );
  }-*/;

  /**
   * WebSql-specific method to open a client-side database.
   *
   * @param name          the name of the database.
   * @param version       the expected version of the database, or an empty string if any version is acceptable. If a
   *                      database with the same name but different version already exists, then an {@code INVALID_STATE_ERR}
   * @param displayName   the name to present to the user
   * @param estimatedSize estimated size — in bytes — of the data that will be stored in the database
   * @see <a href="http://www.w3.org/TR/webdatabase/#dom-opendatabase">W3 Standard</a>
   */
  public static native WebSqlDatabase openDatabase(String name, String version, String displayName, int estimatedSize) /*-{
    return $wnd.openDatabase(name, version, displayName, estimatedSize);
  }-*/;

  /**
   * WebSql-specific method to begin an asynchronous transaction/
   *
   * @param callback
   * @return
   */
  public native void transaction(WebSqlTransactionCallback callback) /*-{
    this.transaction(function(tx) {
      callback.@com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback::begin(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlTransaction;)(tx);
    }, function(e) {
      callback.@com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback::onError(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlException;)(
          @com.bedatadriven.rebar.sql.client.websql.WebSqlException::new(Ljava/lang/String;I)(e.message,e.code));
    }, function() {
      callback.@com.bedatadriven.rebar.sql.client.websql.WebSqlTransactionCallback::onSuccess()();
    });
  }-*/;


}