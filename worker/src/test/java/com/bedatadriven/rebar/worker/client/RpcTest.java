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

package com.bedatadriven.rebar.worker.client;

import com.bedatadriven.rebar.worker.test.client.MathServiceAsync;
import com.google.gwt.core.client.GWT;
import com.bedatadriven.rebar.worker.test.client.MathService;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Verifies that the MathService is working.
 * This way if the 
 *
 * @author Alex Bertram
 */
public class RpcTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.worker.WorkerTest";
  }

  public void testRpcCall() {

    MathServiceAsync service = (MathServiceAsync)
        GWT.create(MathService.class);
    ServiceDefTarget endpoint = (ServiceDefTarget) service;
    String moduleRelativeURL = GWT.getModuleBaseURL() + "math";
    endpoint.setServiceEntryPoint(moduleRelativeURL);

    service.multiply(6, 7, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        fail(throwable.getMessage());
      }

      @Override
      public void onSuccess(Integer result) {
        assertEquals(42, (int) result);
        finishTest();
      }
    });

    delayTestFinish(5000);

  }
}
