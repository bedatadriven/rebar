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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript Overlay for the {@code SQLResultSet} WebSql interface.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#sqlresultset">W3 Standard</a>
 */
public final class WebSqlResultSet extends JavaScriptObject {

  protected WebSqlResultSet() {
  }

  /**
   *
   * @return  the row ID of the row that the SQLResultSet object's SQL statement inserted into the database,
   *  if the statement inserted a row. If the statement inserted multiple rows, the ID of the last row must
   * be the one returned. If the statement did not insert a row, then the
   * attribute must instead raise an INVALID_ACCESS_ERR exception.
   */
  public native int getInsertId() /*-{
    return this.insertId;
  }-*/;

  /**
   * 
   * @return the <strong>total</strong> number of rows affected during the transaction in progress.
   */
  public native int getRowsAffected() /*-{
    return this.insertId;
  }-*/;

  /**
   *
   * @return a {@link WebSqlResultSetRowList} representing the rows returned, in the order
   * returned by the database. The same object must be returned each time.
   * If no rows were returned, then the object will be empty (its length will be zero).
   */
  public native WebSqlResultSetRowList getRows() /*-{
    return this.rows;
  }-*/;





}
