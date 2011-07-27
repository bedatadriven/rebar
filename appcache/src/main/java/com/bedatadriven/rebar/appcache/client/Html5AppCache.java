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

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

class Html5AppCache implements AppCache {

  public static final int UNCACHED = 0;
  public static final int IDLE = 1;
  public static final int CHECKING = 2;
  public static final int DOWNLOADING = 3;
  public static final int UPDATE_READY = 4;
  public static final int OBSOLETE = 5;

  public static final Status[] STATUS_MAPPING = new Status[] {
      Status.UNCACHED, Status.IDLE, Status.CHECKING, Status.DOWNLOADING, Status.UPDATE_READY, Status.OBSOLETE
  };


  @Override
  public String getImplementation() {
    return "HTML5";
  }

  @Override
  public void ensureCached(final AsyncCallback<Void> callback) {
    if (checkStatus(callback))
      return;

    // otherwise we need to wait until the download is complete.
    // unfortunately there doesn't seem to be a way to determine whether we're busy
    // downloading for the first time, or a new version
     new Timer() {
      @Override
      public void run() {
        if(checkStatus(callback)) {
          this.cancel();
        }
      }
    }.scheduleRepeating(500);

  }

  private boolean checkStatus(AsyncCallback<Void> callback) {
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
}
