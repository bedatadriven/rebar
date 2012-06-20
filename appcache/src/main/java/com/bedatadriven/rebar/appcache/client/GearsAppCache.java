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
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GearsAppCache extends AbstractAppCache {
	

  private ManagedResourceStore store = null;
  
  public GearsAppCache() {

  	// if we already have permission, then go ahead and
  	// initialize the store, we are probably be loaded from the cache
  	try {
	  	if(Factory.getInstance().hasPermission()) {
	  		initStore();
	  	}
  	} catch(Throwable t) {
  		Log.debug("GearsAppCache: Factory.getInstance().hasPermission() threw exception", t);
  		store = null;
  	}
  }
  
  public ManagedResourceStore getStore() {
  	if(store == null) {
  	  initStore();
  	}
  	return store;
  }

	private void initStore() {
	  LocalServer server = Factory.getInstance().createLocalServer();
	  store = server.createManagedStore(GWT.getModuleName());
	  store.setManifestUrl(GWT.getModuleBaseURL() + GWT.getModuleName() + ".gears.manifest");
	  
	  sinkProgressEvent(store, this);
	  sinkCompleteEevent(store, this);
	  
	  Log.info("GearsAppCache initializing, current version = " + store.getCurrentVersion());
  }

  @Override
  public String getImplementation() {
    return "Gears";
  }

  @Override
  public void ensureCached(final AsyncCallback<Void> callback) {
    try {
    	getStore().setEnabled(true);
	    if(currentVersionIsEmpty()) {
	      download(callback);
	    } else {
	      callback.onSuccess(null);
	    }
    } catch(Throwable e) {
    	callback.onFailure(e);
    }
  }

  @Override
  public void ensureUpToDate(final AsyncCallback<Void> callback) {
  	download(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
  }
  

	@Override
  public void removeCache(AsyncCallback<Void> callback) {
		try {
			getStore().setEnabled(true);
			callback.onSuccess(null);
		} catch(Throwable e) {
			callback.onFailure(e);
		}
  }

  private boolean currentVersionIsEmpty() {
    return getStore().getCurrentVersion() == null || getStore().getCurrentVersion().isEmpty();
  }

  @Override
  public Status getStatus() {
  	if(store == null) {
  		return Status.UNCACHED;
  	}
  	
  	Log.debug("GearsAppCache: status = " + getStore().getUpdateStatus() + ", currentVersion = " + getStore().getCurrentVersion());
  	
    switch(getStore().getUpdateStatus()) {
      case ManagedResourceStore.UPDATE_CHECKING:
        return Status.CHECKING;

      case ManagedResourceStore.UPDATE_DOWNLOADING:
        return Status.DOWNLOADING;

      default:
      case ManagedResourceStore.UPDATE_FAILED:
      case ManagedResourceStore.UPDATE_OK:
        if(currentVersionIsEmpty()) {
          return Status.UNCACHED;
          
        } else if (!GWT.getPermutationStrongName().equals(getStore().getCurrentVersion())) {
          return Status.UPDATE_READY;
        
        } else {
          return Status.IDLE;
        }
    }
  }

  private void download(final AsyncCallback<Void> callback) {
  	try {
  		getStore().checkForUpdate();
  	} catch(Throwable e) {
  		callback.onFailure(e);
  	}
  	
  	new Timer() {
      @Override
      public void run() {
      	try {
	      	switch(getStore().getUpdateStatus()) {
	          case ManagedResourceStore.UPDATE_CHECKING:
	          case ManagedResourceStore.UPDATE_DOWNLOADING:
	            break;
	
	          case ManagedResourceStore.UPDATE_FAILED:
	            callback.onFailure(new Exception(getStore().getLastErrorMessage()));
	            this.cancel();
	            break;
	
	          case ManagedResourceStore.UPDATE_OK:
	            callback.onSuccess(null);
	            this.cancel();
	            break;
	        }
      	} catch(Throwable e) {
      		this.cancel();
      		callback.onFailure(e);
      	}
      }
    }.scheduleRepeating(500);

  }
  
	// the wrapper in the GWT gears library is just wrong.
  private static native void sinkProgressEvent(ManagedResourceStore store, GearsAppCache appcache) /*-{
    store.onprogress = function(details) {
      appcache.@com.bedatadriven.rebar.appcache.client.GearsAppCache::fireProgress(II)(details.filesComplete, details.filesTotal);
    };
  }-*/;
  
  private static native void sinkCompleteEevent(ManagedResourceStore store, GearsAppCache appcache) /*-{
	  store.oncomplete = function(details) {
	    appcache.@com.bedatadriven.rebar.appcache.client.GearsAppCache::onComplete()();
	  };
	}-*/;

  private void onComplete() {
  	// This event is fired when a ManagedResourceStore completes an update.
  	// Note that updates can be either started explicitly, by calling checkForUpdate(), 
  	// or implicitly, when resources are served from the store.

  	// An update may or may not result in a new version of the store. 
  	if(getStatus() == Status.UPDATE_READY) {
  		fireUpdateReady();
  	}
  } 
  
  
  
  @Override
  public void checkForUpdate() {
  	getStore().checkForUpdate();
  }

	public static boolean isSupported() {
    return Factory.getInstance() != null;
  }

	@Override
  public boolean isCachedOnStartup() {
		return false;
  }

	@Override
  public boolean requiresPermission() {
	  return true;
  }

  
}
