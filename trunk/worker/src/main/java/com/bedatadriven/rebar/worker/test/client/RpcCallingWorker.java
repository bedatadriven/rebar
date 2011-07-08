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

package com.bedatadriven.rebar.worker.test.client;

import com.bedatadriven.rebar.worker.client.AbstractWorkerEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * A simple Gears Worker for integration testing that makes a call
 * to the MathService using GWT RPC.
 *
 * @author Alex Bertram
 */
public class RpcCallingWorker extends AbstractWorkerEntryPoint {

  public final static String ECHO_BASE_URL = "ECHO_BASE_URL";

  public final static String CALL_SERVICE = "CALL_SERVICE";

  @Override
  public void onMessageReceived(final MessageEvent event) {

    registerUncaughtExceptionHandler(event);

    if (ECHO_BASE_URL.equals(event.getBody())) {
      respondWithBaseUrl(event);
    } else if (CALL_SERVICE.equals(event.getBody())) {
      callService(event);
    } else {
      respondWithError(event);
    }
  }

  private void registerUncaughtExceptionHandler(final MessageEvent event) {
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable e) {
        respondWithException(e, event);
      }
    });
  }

  private void respondWithError(MessageEvent event) {
    getPool().sendMessage("didn't understand your message: " + event.getBody(), event.getSender());
  }

  private void callService(MessageEvent event) {
    //getPool().sendMessage("going to call the service.", event.getSender());
    try {
      doServiceCall(event);
    } catch (Throwable t) {
      getPool().sendMessage("error during service call: " + t.getClass().getName() +
          ": " + t.getMessage(), event.getSender());
    }
  }

  private void respondWithBaseUrl(MessageEvent event) {
    getPool().sendMessage(GWT.getModuleBaseURL(), event.getSender());
  }

  private void respondWithException(Throwable e, MessageEvent event) {
    getPool().sendMessage("Uncaught exception on worker: " +
        e.getClass().getName() + ": " + e.getMessage(), event.getSender());
  }

  private void doServiceCall(final MessageEvent event) {
    MathServiceAsync service = (MathServiceAsync)
        GWT.create(MathService.class);
    ServiceDefTarget endpoint = (ServiceDefTarget) service;
    endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "math");

    service.multiply(7, 6, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        getPool().sendMessage("worker failed: " + throwable.getMessage(),
            event.getSender());
      }

      @Override
      public void onSuccess(Integer result) {
        getPool().sendMessage(Integer.toString(result),
            event.getSender());
      }
    });

  }
}
