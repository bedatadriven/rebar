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

package com.bedatadriven.rebar.appcache.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppCacheEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {

    // the default behavior of most HTML5 app cache implementations
    // seems to be to start downloading the cache immediately,
    // so let's standardize that across browsers / implementations
  	// (except when in hosted mode)
  	if(GWT.isScript()) {
	    AppCache appCache = AppCacheFactory.get();
	    appCache.ensureCached(new AsyncCallback<Void>() {
	      @Override
	      public void onFailure(Throwable caught) {
	        /** NOOP */
	      }
	
	      @Override
	      public void onSuccess(Void result) {
	        /** NOOP **/
	      }
	    });
  	}
  }
}
