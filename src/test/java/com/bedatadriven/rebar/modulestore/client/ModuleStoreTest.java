/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.modulestore.client;

import com.google.gwt.core.client.GWT;
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
