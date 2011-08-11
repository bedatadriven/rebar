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

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Javascript overlay for the {@code SQLTransaction} WebSql interface.
 *
 * @see <a href="http://www.w3.org/TR/webdatabase/#sqltransaction">W3 Standard</a>
 */
public final class WebSqlTransaction extends JavaScriptObject implements SqlTransaction {

  protected WebSqlTransaction() {
  }


  public native void executeSql(String statement) /*-{
    this.executeSql(statement, [] );
  }-*/;

  public native void executeSql(String statement, JavaScriptObject parameters) /*-{
    this.executeSql(statement, parameters);
  }-*/;


  public void executeSql(String statement, Object[] parameters) {
    executeSql(statement, toParamArray(parameters));
  }

  public native void executeSql(String statement, JavaScriptObject parameters, WebSqlResultCallback callback)/*-{
    this.executeSql(statement, parameters, function(tx, results) {
      callback.@com.bedatadriven.rebar.sql.client.websql.WebSqlResultCallback::onSuccess(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlTransaction;Lcom/bedatadriven/rebar/sql/client/websql/WebSqlResultSet;)(tx, results);
    }, function(e) {
      callback.@com.bedatadriven.rebar.sql.client.websql.WebSqlResultCallback::onFailure(Lcom/bedatadriven/rebar/sql/client/websql/WebSqlException;)(
          @com.bedatadriven.rebar.sql.client.websql.WebSqlException::new(Ljava/lang/String;I)(e.message,e.code));
    });
  }-*/;

  public void executeSql(String statement, Object[] parameters, WebSqlResultCallback callback) {
    executeSql(statement, toParamArray(parameters), callback);
  }

  public void executeSql(String statement, WebSqlResultCallback resultCallback) {
    executeSql(statement, JavaScriptObject.createArray(), resultCallback);
  }

  @Override
  public void executeSql(String statement, Object[] parameters, final SqlResultCallback callback) {
    executeSql(statement, parameters, new WebSqlResultCallback() {
      @Override
      public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
        callback.onSuccess(tx, results.toSqlResultSet());
      }

      @Override
      public boolean onFailure(WebSqlException e) {
        return callback.onFailure(e);
      }
    });
  }
  

  @Override
  public void executeSql(String statement, SqlResultCallback resultCallback) {
    executeSql(statement, new Object[] {}, resultCallback);
  }

  private ParamArray toParamArray(Object[] parameters) {
    ParamArray paramArray = JsArray.createArray().cast();
    for(Object param : parameters) {
      if(param instanceof Number) {
        paramArray.push(((Number)param).doubleValue());
      } else if(param instanceof String) {
        paramArray.push((String)param);
      } else if(param instanceof Date) {
      	paramArray.push(Long.toString(((Date)param).getTime()));
      } else {
        throw new IllegalArgumentException("Param argument of '" + param.getClass().getName() + "'");
      }
    }
    return paramArray;
  }


  private static final class ParamArray extends JavaScriptObject {

    protected ParamArray() {
    }

    public native void push(double x) /*-{
      this.push(x);
    }-*/;

    public native void push(String s) /*-{
      this.push(s);
    }-*/;

    public native void push(boolean b) /*-{
      this.push(b);
    }-*/;

  }

}
