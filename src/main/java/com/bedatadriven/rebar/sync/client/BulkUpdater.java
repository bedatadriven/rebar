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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Generic interface to a service capable of executing a set of SQL statements
 * asynchronously
 */
public interface BulkUpdater {

  /**
   * Executes a list of BulkOperation objects asynchronously within a savepoint, such that
   * all of the statements fail or succeed together.
   *
   * On success, the callback will receive the total number of update rows.
   *
   * @param bulkOperationJsonArray
   * @param callback
   */
  public void executeUpdates(String bulkOperationJsonArray, AsyncCallback<Integer> callback);

}
