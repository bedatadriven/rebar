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
 * Javascript Overlay for the WebSql {@code SQLResultSetRowList} interface.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#sqlresultsetrowlist">W3 standard</a>
 */
public final class WebSqlResultSetRowList extends JavaScriptObject {

  protected WebSqlResultSetRowList() {
  }

  /**
   *
   * @return the number of rows in this row list.
   */
  public native int length() /*-{
    return this.length;
  }-*/;

  /**
   * Gets the row at the given index.
   *
   * Each row must be represented by a native ordered dictionary data type. In the JavaScript binding,
   * this must be Object. Each row object must have one property (or dictionary entry) per column,
   * with those properties enumerating in the order that these columns were returned by the database.
   * Each property must have the name of the column and the value of the cell, as they were returned by the database.
   *
   * @param index the row index
   * @return the row object
   */
  public native WebSqlResultSetRow getRow(int index) /*-{
    return this.item(index);
  }-*/;
}
