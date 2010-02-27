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

package com.bedatadriven.rebar.worker.test.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface for the MathService, which is used
 * for gears/gwt integration testing.
 *
 * (This belongs in the test/src/java root but the gwt-maven-plugin
 *  can't find it there)
 */
public interface MathServiceAsync {

  void multiply(int a, int b, AsyncCallback<Integer> callback);
}
