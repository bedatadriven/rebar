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

public interface AppCache {


  public enum Status {

    /**
     * AppCache is not supported by this browser
     */
    UNSUPPORTED,

    /**
     * The ApplicationCache object's cache host is not associated with an application cache at this time.
     */
    UNCACHED,

    /**
     * The ApplicationCache object's cache host is associated with an application
     * cache whose application cache group's update status is idle, and that
     *  application cache is the newest cache in its application cache group, and the
     * application cache group is not marked as obsolete.
     */
    IDLE,

    /**
     * Checking for the most recent version
     */
    CHECKING,

    /**
     * Downloading a new version of the application
     */
    DOWNLOADING,

    /**
     * A new version has been successfully downloaded and is ready
     * for the user upon page refresh
     */
    UPDATE_READY,

    /**
     * The cache manifest has disappeared from the server.
     * (Not reported by Gears)
     */
    OBSOLETE
  }

  /**
   * @return a user-friendly description of the cache API being
   * used.
   */
  String getImplementation();

  /**
   * Ensures that the current application is completely cached and
   * ready to serve offline.
   *
   * This does not necessary trigger an update.
   *
   * @param callback an asynchronous callback that will receive the version upon completion
   */
  void ensureCached(AsyncCallback<Void> callback);

  /**
   * 
   * @return the current status of the application cache.
   */
  Status getStatus();
}
