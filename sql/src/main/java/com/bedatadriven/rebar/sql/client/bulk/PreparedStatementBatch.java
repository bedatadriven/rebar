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

package com.bedatadriven.rebar.sql.client.bulk;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

import java.util.Date;

/**
 * A JavaScript overlay for classes 
 *
 *
 * @author Alex Bertram
 */
public final class PreparedStatementBatch extends JavaScriptObject {

  protected PreparedStatementBatch() {

  }

  public static native PreparedStatementBatch newInstance() /*-{
    return { executions: [] };
  }-*/;

  public static native JsArray<PreparedStatementBatch> fromJson(String json) /*-{
  	if($wnd.JSON) {
  		return $wnd.JSON.parse(json);
  	} else {
    	return eval('(' + json + ')');
   	}
  }-*/;

  /**
   * Gets the parametrized SQL statement to execute.

   * @return the parametrized SQL statement
   */
  public native String getStatement() /*-{
        return this.statement;
    }-*/;

  /**
   * Sets the parametrized SQL statement. For example:
   * <p/>
   * <ul>
   * <li><code>UPDATE MyTable SET Name = ? WHERE ID = ?</code></li>
   * <li><code>INSERT INTO MyTable (ID, NAME) VALUES (?, ?)</li>
   * <li><code>
   * </ul>
   *
   * @param sql the parametrized SQL statement
   */
  public native void setStatement(String sql) /*-{
        this.statement = sql;
    }-*/;

  /**
   * Gets the list of executions. An execution is a complete set of
   * parameters for the statement.
   *
   * @return
   */
  public native JsArray<JsArrayString> getExecutions() /*-{
        return this.executions;
  }-*/;

  public native void setExecutions(JsArray<JsArrayString> executions) /*-{
        this.executions = executions;
  }-*/;

  public void addExecution(Object... parameters) {
    JsArrayString array = JsArray.createArray().cast();
    for(int i=0; i!=parameters.length; ++i) {
      Object param = parameters[i];
      if(param instanceof Date)
        array.push(Long.toString(((Date)param).getTime()));
      else
        array.push(param.toString());
    }
    getExecutions().push(array);
  }
}
