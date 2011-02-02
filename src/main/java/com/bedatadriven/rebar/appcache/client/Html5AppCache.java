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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Html5AppCache implements AppCache {

  public static final int UNCACHED = 0;
  public static final int IDLE = 1;
  public static final int CHECKING = 2;
  public static final int DOWNLOADING = 3;
  public static final int UPDATE_READY = 4;
  public static final int OBSOLETE = 5;


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
      status = getStatus();
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

  public static native boolean isSupported() /*-{
    return typeof $wnd.applicationCache == 'object';
  }-*/;

   public static native int update() /*-{
    return $wnd.applicationCache.update();
  }-*/;

  public static native int getStatus() /*-{
    return $wnd.applicationCache.status;
  }-*/;
}
