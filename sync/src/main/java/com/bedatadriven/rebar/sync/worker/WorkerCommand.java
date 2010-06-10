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

package com.bedatadriven.rebar.sync.worker;

import com.bedatadriven.rebar.sync.client.PreparedStatementBatch;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 *
 * @author Alex Bertram
 */
public final class WorkerCommand extends JavaScriptObject {

  protected WorkerCommand() {
    // required for overlay types
  }

  public static native WorkerCommand newInstance(int executionId) /*-{
    return { executionId: executionId, operations: [ ] } ;
  }-*/;

  public native String getDatabaseName() /*-{
    return this.databaseName;
  }-*/;

  public native void setDatabaseName(String name) /*-{
    this.databaseName = name;
  }-*/;

  public native JsArray<PreparedStatementBatch> getOperations() /*-{
    return this.operations;
  }-*/;

  public native void setOperations(String json) /*-{
    this.operations = eval(json);
  }-*/;

  public native int getExecutionId() /*-{
    return this.executionId;
  }-*/;


  public native String getExecutionIdAsString() /*-{
    return this.executionId;
  }-*/;


  public void addOperation(PreparedStatementBatch operation) {
    getOperations().push(operation);
  }
}
