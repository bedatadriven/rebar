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


import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.appcache.client.AppCache.Status;
import com.bedatadriven.rebar.appcache.client.events.ProgressEventHandler;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEventHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Html5AppCache extends AbstractAppCache {

	public static final String DISABLE_COOKIE_NAME = "HTML5_NO_APPCACHE";
	public static final String DISABLE_COOKIE_VALUE = "DISABLE";
	
  public static final int UNCACHED = 0;
  public static final int IDLE = 1;
  public static final int CHECKING = 2;
  public static final int DOWNLOADING = 3;
  public static final int UPDATE_READY = 4;
  public static final int OBSOLETE = 5;
  
  public static final Status[] STATUS_MAPPING = new Status[] {
      Status.UNCACHED, Status.IDLE, Status.CHECKING, Status.DOWNLOADING, Status.UPDATE_READY, Status.OBSOLETE
  };
  
  private int errorCount = 0;
  

  @Override
  public String getImplementation() {
    return "HTML5";
  }
 
  @Override
  public void ensureCached(final AsyncCallback<Void> callback) {
     	
  	Cookies.removeCookie(DISABLE_COOKIE_NAME);
  	
  	if (callbackIfCached(callback))
      return;

	  // otherwise we need to wait until the download is complete.
    // unfortunately there doesn't seem to be a way to determine whether we're busy
    // downloading for the first time, or a new version
     new Timer() {
      @Override
      public void run() {
        if(callbackIfCached(callback)) {
          this.cancel();
        }
      }
    }.scheduleRepeating(500);
  } 

	private boolean callbackIfCached(AsyncCallback<Void> callback) {
    int status = 0;
    try {
      status = getAppCacheStatus();
    } catch (Exception e) {
      callback.onFailure(new AppCacheException(e.getMessage()));
    }
    switch(status) {
      case IDLE:
      case UPDATE_READY:
        callback.onSuccess(null);
        return true;
      case UNCACHED:
        callback.onFailure(new AppCacheException("There is no manifest attached to this application (Status = UNCACHED)"));
        return true;
      case OBSOLETE:
        callback.onFailure(new AppCacheException("The attached manifest no longer exists on the server (Status = OBSOLETE"));
        return true;
    }
    return false;
  }

  @Override
  public void ensureUpToDate(final AsyncCallback<Void> callback) {
  	
  	Cookies.removeCookie(DISABLE_COOKIE_NAME);
  	
  	
  	// an error retrieving the cache does not neccessarily
  	// alter the status if it is just a network problem.
  	// but we want to make sure that we have an answer so we
  	// have to trap the errors ourselves
  	sinkErrorEvent(this);
  	final ErrorListener errors = new ErrorListener();
  
  	if(getAppCacheStatus() == IDLE || 
  		 getAppCacheStatus() == UPDATE_READY) {
  
  		try {
    		update();
  		} catch(Exception e) {
  			Log.error("Html5AppCache: call to update() threw exception. Current state = " + 
  						getAppCacheStatus(), e);
    		callback.onFailure(e);
    		return;
    	}
  	}
  	
	  new Timer() {
      @Override
      public void run() {
    	
        switch(getAppCacheStatus()) {
        case IDLE:
        	if(errors.haveOccurred()) {
        		callback.onFailure(new AppCacheException("AppCache connection failure"));
        	} else {
        		callback.onSuccess(null);
        	}
        	this.cancel();
        	break;
        	
        case UPDATE_READY:
          callback.onSuccess(null);
          this.cancel();
          break;
        case UNCACHED:
          callback.onFailure(new AppCacheException("There is no manifest attached to this application (Status = UNCACHED)"));
          this.cancel();
          break;
        case OBSOLETE:
          callback.onFailure(new AppCacheException("The attached manifest no longer exists on the server (Status = OBSOLETE"));
          this.cancel();
          break;
        }
    	}
    }.scheduleRepeating(500);
  }
 

	@Override
  public void removeCache(final AsyncCallback<Void> callback) {
    int status = 0;
    try {
      status = getAppCacheStatus();

	    if(status == UNCACHED || status == OBSOLETE) {
	    	callback.onSuccess(null);
	    } else {
	    	Cookies.setCookie(DISABLE_COOKIE_NAME, DISABLE_COOKIE_VALUE);
	    	update();
	    
	    	 new Timer() {
	         @Override
	         public void run() {
	       	
	           switch(getAppCacheStatus()) {
	           case IDLE:
	           case UPDATE_READY:
	          	 callback.onFailure(new AppCacheException("Connection problem prevented cache from being marked as obsolete"));
	          	 this.cancel();
	          	 Cookies.removeCookie(DISABLE_COOKIE_NAME);
	             break;
	           case UNCACHED:
	           case OBSOLETE:
	             callback.onSuccess(null);
	             this.cancel();
	             Cookies.removeCookie(DISABLE_COOKIE_NAME);
	             break;
	           }
	       	}
	       }.scheduleRepeating(500);
	    	
	    }
    } catch (Exception e) {
      callback.onFailure(new AppCacheException(e.getMessage()));
    }  
  }
	
  
	@Override
  public Status getStatus() {
    return STATUS_MAPPING[getAppCacheStatus()];
  }

  /**
   * 
   * @return true if the browser supports the AppCache API
   */
  public static native boolean isSupported() /*-{
    return typeof $wnd.applicationCache == 'object';
  }-*/;

  public static native int update() /*-{
    return $wnd.applicationCache.update();
  }-*/;

  public static native int getAppCacheStatus() /*-{
    return $wnd.applicationCache.status;
  }-*/;
  
  /**
   * 
   * @return true if the document has an AppCache manifest attached
   */
  public static boolean hasManifest() {
  	return Document.get().getDocumentElement().hasAttribute("manifest");
  }
	
	@Override
  protected void sinkProgressEvents() {
	  sinkProgressEvent(this);
  }

	private static native void sinkProgressEvent(Html5AppCache appCache) /*-{
		$wnd.applicationCache.onprogress = function(event) {
			appCache.@com.bedatadriven.rebar.appcache.client.AbstractAppCache::fireProgress(II)(event.loaded, event.total);
		}
	}-*/;

	@Override
  protected void sinkUpdateReadyEvents() {
		sinkUpdateReadyEvent(this);
  }

	private static native void sinkUpdateReadyEvent(Html5AppCache appCache) /*-{
		$wnd.applicationCache.onupdateready = function(event) {
			appCache.@com.bedatadriven.rebar.appcache.client.AbstractAppCache::fireUpdateReady()();
		}
	}-*/;

	private static native void sinkErrorEvent(Html5AppCache appCache) /*-{
		$wnd.applicationCache.onerror = function(event) {
			appCache.@com.bedatadriven.rebar.appcache.client.Html5AppCache::onError()();
		}
	}-*/;

	private void onError() {
		errorCount++;
	}
	
	private class ErrorListener {
		private int initialCount = errorCount;
		
		public boolean haveOccurred() {
			return errorCount > initialCount;
		}
	}
	
	@Override
  public boolean isCachedOnStartup() {
		return true;
  }

	@Override
  public boolean requiresPermission() {
	  return true;
  }

	@Override
  public void checkForUpdate() {
		update();
  }

	
}
