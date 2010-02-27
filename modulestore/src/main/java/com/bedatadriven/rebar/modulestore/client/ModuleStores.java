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

package com.bedatadriven.rebar.modulestore.client;

import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.core.client.GWT;

/**
 *
 * Convenience class for accessing this Module's <code>ManagedResourceStore</code>s
 *
 * @author Alex Bertram
 */
public class ModuleStores {

  /**
   * Gets the <code>ManagedResourceStore</code> common to all permutations of the module.
   * Includes selection script, resources in the public folder.
   *
   * @return the <code>ManagedResourceStore</code> common to all permutations of the module.
   *
   */
  public static ManagedResourceStore getCommon() {
    LocalServer server = Factory.getInstance().createLocalServer();
    ManagedResourceStore store = server.createManagedStore(GWT.getModuleName());
    store.setManifestUrl(getCommonManifestUrl());
    store.checkForUpdate();
    return store;
  }



  /**
   * Gets the permutations-specific <code>ManagedResourceStore</code>
   *
   * @return the permutations-specific <code>ManagedResourceStore</code>
   */
  public static ManagedResourceStore getPermutation() {
    LocalServer server = Factory.getInstance().createLocalServer();
    ManagedResourceStore store = server.createManagedStore(GWT.getPermutationStrongName());
    store.setManifestUrl(getPermutationManifestUrl());
    store.checkForUpdate();
    return store;
  }

  /**
   *
   * Checks the offline availablity of the permutations-specific javascript.
   *
   * @return True if the permutation-specific <code>ManagedResourceStore</code> has
   * already been created and downloaded.
   */
  public static boolean isPermutationAvailable() {
    LocalServer server = Factory.getInstance().createLocalServer();
    try {
      return server.canServeLocally(getPermutationManifestUrl());
    } catch (GearsException e) {
      return false;
    }
  }

  private static String getCommonManifestUrl() {
    return GWT.getModuleBaseURL() + GWT.getModuleName() + ".nocache.manifest";
  }

  private static String getPermutationManifestUrl() {
    return GWT.getModuleBaseURL() + GWT.getPermutationStrongName() + ".cache.manifest";
  }
}
