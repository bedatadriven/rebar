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

import org.mortbay.log.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

class GearsAppCache implements AppCache  {

  private ManagedResourceStore store;

  public GearsAppCache() {
    LocalServer server = Factory.getInstance().createLocalServer();
    this.store = server.createManagedStore(GWT.getModuleName());
    this.store.setManifestUrl(GWT.getModuleBaseURL() + GWT.getModuleName() + ".gears.manifest");
    
    Log.info("GearsAppCache initializing, current version = " + store.getCurrentVersion());
  }

  @Override
  public String getImplementation() {
    return "Gears";
  }

  @Override
  public void ensureCached(final AsyncCallback<Void> callback) {
    store.setEnabled(true);
    if(currentVersionIsEmpty()) {
      download(callback);
    } else {
      callback.onSuccess(null);
    }
  }

  private boolean currentVersionIsEmpty() {
    return store.getCurrentVersion() == null || store.getCurrentVersion().isEmpty();
  }

  @Override
  public Status getStatus() {
  	Log.debug("GearsAppCache: status = " + store.getUpdateStatus() + ", currentVersion = " + store.getCurrentVersion());
  	
    switch(store.getUpdateStatus()) {
      case ManagedResourceStore.UPDATE_CHECKING:
        return Status.CHECKING;

      case ManagedResourceStore.UPDATE_DOWNLOADING:
        return Status.DOWNLOADING;

      default:
      case ManagedResourceStore.UPDATE_FAILED:
      case ManagedResourceStore.UPDATE_OK:
        if(currentVersionIsEmpty()) {
          return Status.UNCACHED;
        } else if (!GWT.getPermutationStrongName().equals(store.getCurrentVersion())) {
          return Status.UPDATE_READY;
        } else {
          return Status.IDLE;
        }
    }
  }

  private void download(final AsyncCallback<Void> callback) {
    store.checkForUpdate();
    new Timer() {
      @Override
      public void run() {
        switch(store.getUpdateStatus()) {
          case ManagedResourceStore.UPDATE_CHECKING:
            break;
          case ManagedResourceStore.UPDATE_DOWNLOADING:
            break;
          case ManagedResourceStore.UPDATE_FAILED:
            callback.onFailure(new Exception(store.getLastErrorMessage()));
            this.cancel();
            break;
          case ManagedResourceStore.UPDATE_OK:
            callback.onSuccess(null);
            this.cancel();
            break;
        }
      }
    }.scheduleRepeating(500);
  }

  public ManagedResourceStore getStore() {
    return store;
  }

  public static boolean isSupported() {
    return Factory.getInstance() != null;
  }

}
