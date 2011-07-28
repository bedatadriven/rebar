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

package com.bedatadriven.rebar.worker.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.gears.client.httprequest.RequestCallback;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * Superclass for <code>RemoteServiceProxy</code>s in worker threads that use the Gears
 * <code>HttpRequest</code> object rather than GWT's <code>RequestBuilder</code> API,
 * which is unavailable in the worker thread.
 *
 * @author Alex Bertram
 */
public class GearsServiceProxy extends RemoteServiceProxy {


  public GearsServiceProxy(String moduleBaseURL, String remoteServiceRelativePath, String serializationPolicyName, Serializer serializer) {
    super(moduleBaseURL, remoteServiceRelativePath, serializationPolicyName, serializer);
  }

  /**
   * @return
   */
  protected native Factory getFactory() /*-{
        if($wnd) {
            return $wnd.google && $wnd.google.gears && $wnd.google.gears.factory;
        } else {
            return google.gears.factory;
        }
    }-*/;


  
  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Override
  protected <T> Request doInvoke(ResponseReader responseReader,
			String methodName, RpcStatsContext statsContext, String requestData,
			AsyncCallback<T> callback) {

    try {
      Factory factory = getFactory();
      final HttpRequest httpRequest = factory.createHttpRequest();
      final Request request = new RequestAdapter(httpRequest);

      final RequestCallbackAdapter<T> handler = new RequestCallbackAdapter<T>(
          this, methodName, statsContext,
          callback, responseReader);

      httpRequest.open("POST", getServiceEntryPoint());
      httpRequest.setRequestHeader("Content-Type", "text/x-gwt-rpc; charset=utf-8");
      httpRequest.setRequestHeader(RpcRequestBuilder.STRONG_NAME_HEADER, GWT.getPermutationStrongName());
      httpRequest.setRequestHeader(RpcRequestBuilder.MODULE_BASE_HEADER, GWT.getModuleBaseURL());

      httpRequest.send(requestData, new RequestCallback() {
        @Override
        public void onResponseReceived(HttpRequest httpRequest) {
          handler.onResponseReceived(request, new ResponseAdapter(httpRequest));
        }
      });
      return request;
    } catch (Throwable t) {
      callback.onFailure(new InvocationException("could not initiate request", t));
    }
    return null;
  }
}
