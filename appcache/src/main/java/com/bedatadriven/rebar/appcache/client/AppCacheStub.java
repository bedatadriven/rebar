package com.bedatadriven.rebar.appcache.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A stub implementation of the AppCache for use during host mode. 
 * 
 */
class AppCacheStub extends AbstractAppCache {

	@Override
  public String getImplementation() {
	  return "HostedMode";
  }

	@Override
  public void ensureCached(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
  }

	@Override
  public void ensureUpToDate(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
  }

	@Override
  public Status getStatus() {
	  return AppCache.Status.IDLE;
  }

	@Override
  public void checkForUpdate() {

  }

	@Override
  public boolean isCachedOnStartup() {
	  return false;
  }

	@Override
  public boolean requiresPermission() {
	  return true;
  }

	@Override
  public void removeCache(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
  }

}
