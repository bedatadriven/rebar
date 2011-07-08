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
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class ModuleStoreTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.modulestore.ModuleStore";
  }

  public void testCommon() {
    final ManagedResourceStore store = ModuleStores.getCommon();
    downloadStore(store);
    assertTrue(store.getName().startsWith("com.bedatadriven.rebar.modulestore.ModuleStore"));
  }

  public void testPermutationSpecific() {
    final ManagedResourceStore store = ModuleStores.getPermutation();
    downloadStore(store);
  }

  private void downloadStore(final ManagedResourceStore store) {
    new Timer() {
      @Override
      public void run() {
        switch (store.getUpdateStatus()) {
          case ManagedResourceStore.UPDATE_OK:
            finishTest();
            break;
          case ManagedResourceStore.UPDATE_CHECKING:
          case ManagedResourceStore.UPDATE_DOWNLOADING:
            schedule(500);
            break;
          case ManagedResourceStore.UPDATE_FAILED:
            fail(store.getLastErrorMessage());
            break;
        }
      }
    }.schedule(500);
    delayTestFinish(5000);
  }
}
