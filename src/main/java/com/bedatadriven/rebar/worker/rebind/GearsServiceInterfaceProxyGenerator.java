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

package com.bedatadriven.rebar.worker.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.bedatadriven.rebar.worker.client.rpc.GearsServiceProxy;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.rebind.rpc.ProxyCreator;
import com.google.gwt.user.rebind.rpc.ServiceInterfaceProxyGenerator;

/**
 * Changes the default behavior of the <code>ServiceInterfaceProxyGenerator</code> to create the actual
 * proxy class with <code>WorkerProxyCreator</code>.
 * <p/>
 * The point of all of this is to generate the proxy with a super class of <code>WorkerServiceProxy</code>,
 * which uses the Gears <code>HttpRequest</code> API rather than the GWT's HttpRequest API, which
 * isn't available from the worker thread.
 *
 * @author Alex Bertram
 */
public class GearsServiceInterfaceProxyGenerator extends ServiceInterfaceProxyGenerator {

  @Override
  protected ProxyCreator createProxyCreator(JClassType remoteService) {
    return new ProxyCreator(remoteService) {
      @Override
      protected Class<? extends RemoteServiceProxy> getProxySupertype() {
        return GearsServiceProxy.class;
      }
    };
  }
}
