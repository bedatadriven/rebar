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

import com.google.gwt.user.client.rpc.AsyncCallback;

class NullAppCache extends AbstractAppCache {

  @Override
  public String getImplementation() {
    return "Unsupported";
  }

  @Override
  public void ensureCached(AsyncCallback<Void> callback) {
    callback.onFailure(new AppCacheException(AppCacheErrorType.UNSUPPORTED));
  }
  
  @Override
  public void ensureUpToDate(AsyncCallback<Void> callback) {
  	callback.onFailure(new AppCacheException(AppCacheErrorType.UNSUPPORTED));
  }

	@Override
  public Status getStatus() {
    return Status.UNSUPPORTED;
  }

	@Override
  public boolean isCachedOnStartup() {
	  return false;
  }

	@Override
  public boolean requiresPermission() {
	  return false;
  }

	@Override
  public void checkForUpdate() {
	  
  }

	@Override
  public void removeCache(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
  }

}
