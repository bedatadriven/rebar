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

import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.workerpool.WorkerPool;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Alex Bertram
 */
public class RpcCallingWorkerTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.worker.WorkerTest";
  }


  public void testRpcCallingWorkerBaseUrl() {

    WorkerPool pool = Factory.getInstance().createWorkerPool();
    pool.setMessageHandler(new WorkerPoolMessageHandler() {
      @Override
      public void onMessageReceived(MessageEvent event) {
        assertEquals(GWT.getModuleBaseURL(), event.getBody());
        finishTest();
      }
    });
    int workerId = pool.createWorkerFromUrl(GWT.getModuleBaseURL() + "RpcCallingWorker/RpcCallingWorker.js");
    pool.sendMessage(RpcInitializationMessage.newInstance(), workerId);
    pool.sendMessage("ECHO_BASE_URL", workerId);

    delayTestFinish(5000);
  }


  public void testRpcCallingWorker() {

    WorkerPool pool = Factory.getInstance().createWorkerPool();
    pool.setMessageHandler(new WorkerPoolMessageHandler() {
      @Override
      public void onMessageReceived(MessageEvent event) {

        assertEquals("42", event.getBody());
        finishTest();
      }
    });
    int workerId = pool.createWorkerFromUrl(GWT.getModuleBaseURL() + "RpcCallingWorker/RpcCallingWorker.js");
    pool.sendMessage(RpcInitializationMessage.newInstance(), workerId);
    pool.sendMessage("CALL_SERVICE", workerId);

    delayTestFinish(1000);

  }

}
