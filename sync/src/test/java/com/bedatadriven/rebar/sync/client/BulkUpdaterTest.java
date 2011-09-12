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

package com.bedatadriven.rebar.sync.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BulkUpdaterTest extends BaseTestCase {


  public void testExecutorWithJson() throws Exception {

    BulkUpdaterAsync updater = GWT.create(BulkUpdaterAsync.class);
    updater.executeUpdates(dbName, json, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        Window.alert("failed: " + throwable.getMessage());
        fail(throwable.getMessage());
      }

      @Override
      public void onSuccess(Integer rowsAffected) {
        assertEquals(4, (int)rowsAffected);
        finishTest();
      }
    });

    delayTestFinish(10000);

  }


}