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

import com.google.gwt.core.shared.GWT;

import java.util.logging.Logger;

public class AppCacheFactory {

  private static final Logger LOGGER = Logger.getLogger(AppCacheFactory.class.getName());

  private static AppCache instance = null;

  public static AppCache get() {
    if (instance == null) {
      if (!GWT.isScript()) {

        LOGGER.fine("Creating AppCacheStub for Dev Mode");
        instance = new AppCacheStub();

      } else if (Html5AppCache.isSupported() && Html5AppCache.hasManifest()) {
        LOGGER.fine("Creating Html5AppCache");

        instance = new Html5AppCache();

      } else {
        LOGGER.fine("Creating Null AppCache");

        instance = new NullAppCache();
      }
    }
    return instance;
  }

}
