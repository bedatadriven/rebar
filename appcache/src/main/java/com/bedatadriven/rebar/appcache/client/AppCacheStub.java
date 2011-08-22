package com.bedatadriven.rebar.appcache.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A stub implementation of the AppCache for use during host mode. 
 * 
 */
class AppCacheStub implements AppCache {

	@Override
  public String getImplementation() {
	  return "HostedMode";
  }

	@Override
  public void ensureCached(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
  }

	@Override
  public Status getStatus() {
	  return AppCache.Status.IDLE;
  }

}
