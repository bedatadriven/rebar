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

package com.bedatadriven.rebar.sql.client.gears.worker;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author Alex Bertram
 */
public final class WorkerResponse extends JavaScriptObject {

  public static int EXCEPTION = 1;
  public static int LOG = 2;
  public static int SUCCESS = 3;


  protected WorkerResponse() {
  }

  public static String newExceptionResponse(int executionId, String message) {
  	JSONObject response = new JSONObject();
  	response.put("executionId", new JSONNumber(executionId));
  	response.put("message", new JSONString(message));
  	response.put("type", new JSONNumber(EXCEPTION));
  	return response.toString();
  }

  public static String newLogResponse(int executionId, String message) {
  	JSONObject response = new JSONObject();
  	response.put("executionId", new JSONNumber(executionId));
  	response.put("message", new JSONString(message));
  	response.put("type", new JSONNumber(LOG));
  	return response.toString();
  }

  public static String newSuccessResponse(int executionId, int rowsAffected) {
  	JSONObject response = new JSONObject();
  	response.put("executionId", new JSONNumber(executionId));
  	response.put("rowsAffected", new JSONNumber(rowsAffected));
  	response.put("type", new JSONNumber(SUCCESS));
  	return response.toString();
  }

  public static native WorkerResponse parse(String json) /*-{
  	return eval('(' + json + ')');
  }-*/;
    
  public native int getExecutionId() /*-{
    return this.executionId;
  }-*/;

  public native int getType() /*-{
    return this.type;
  }-*/;

  public native String getMessage() /*-{
    return this.message;
  }-*/;

  public native int getRowsAffected() /*-{
    return this.rowsAffected;
  }-*/;


}
