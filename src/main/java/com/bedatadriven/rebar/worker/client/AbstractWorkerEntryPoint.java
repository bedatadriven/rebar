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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gears.client.workerpool.WorkerPool;
import com.google.gwt.gears.client.workerpool.WorkerPoolMessageHandler;

/**
 * @author Alex Bertram
 */
public abstract class AbstractWorkerEntryPoint implements EntryPoint, WorkerPoolMessageHandler {

  @Override
  public void onModuleLoad() {
    registerWorker();
  }

  private static void fireOnMessage(WorkerPoolMessageHandler handler,
                                    MessageEvent event) {
    if (RpcInitializationMessage.isA(event.getBodyObject())) {
      RpcInitializationMessage msg = event.getBodyObject().cast();
      setModuleBase(msg.getModuleBaseURL());
    } else {
      handler.onMessageReceived(event);
    }
  }

  private static native void setModuleBase(String path) /*-{
        $moduleBase = path;
    }-*/;


  private native void registerWorker() /*-{
        var handler = this;
        google.gears.workerPool.onmessage = function(a, b, message) {
            @com.bedatadriven.rebar.worker.client.AbstractWorkerEntryPoint::fireOnMessage(Lcom/google/gwt/gears/client/workerpool/WorkerPoolMessageHandler;Lcom/google/gwt/gears/client/workerpool/WorkerPoolMessageHandler$MessageEvent;)(handler,message);
        };
    }-*/;

  /**
   * @return The worker pool to which this worker belongs.
   */
  protected native WorkerPool getPool() /*-{
        return google.gears.workerPool;
    }-*/;

  public void sendMessage(boolean body, int workerId) {
    getPool().sendMessage(body, workerId);
  }

  public void sendMessage(double body, int workerId) {
    getPool().sendMessage(body, workerId);
  }

  public void sendMessage(JavaScriptObject javaScriptObject, int workerId) {
    getPool().sendMessage(javaScriptObject, workerId);
  }

  public void sendMessage(String body, int workerId) {
    getPool().sendMessage(body, workerId);
  }

  /**
   * Called upon reciept of message.
   *
   * @param event The Message Event
   */
  public void onMessageReceived(MessageEvent event) {

  }


}
