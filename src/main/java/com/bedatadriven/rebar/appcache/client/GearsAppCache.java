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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GearsAppCache implements AppCache {

  private ManagedResourceStore store;
  private HandlerManager handlerManager;
  private String initialVersion;

  public GearsAppCache() {
    LocalServer server = Factory.getInstance().createLocalServer();
    this.store = server.createManagedStore(GWT.getModuleName());
    this.store.setManifestUrl(GWT.getModuleBaseURL() + "gears.manifest");
  }

  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  @Override
  public String getImplementation() {
    return "Gears";
  }

  @Override
  public Status getStatus() {
    switch( store.getUpdateStatus() ){

      case 1:
        return Status.CHECKING;
      case 2:
        return Status.DOWNLOADING;

      default:
      case 0: // Update OK
      case 3: // Update failed
        if( store.getCurrentVersion() == null || store.getCurrentVersion().length() == 0) {
          return Status.UNCACHED;
        } else {
          return Status.IDLE;
        }
    }
  }

  @Override
  public void ensureCached(final AsyncCallback<Void> callback) {
    store.setEnabled(true);
    if(store.getCurrentVersion() == null || store.getCurrentVersion().isEmpty()) {
      download(callback);
    } else {
      callback.onSuccess(null);
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
}
