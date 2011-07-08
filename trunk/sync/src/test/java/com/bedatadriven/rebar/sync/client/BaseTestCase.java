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

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;

public abstract class BaseTestCase extends GWTTestCase {

  protected String dbName;
  protected static final String json =
      "[ { statement: \"create table mytest (number int)\" }, "  +
        "{ statement: \"insert into mytest (number) values (?)\", executions: [ [1], [2], [3], [4] ] } " +
      "]";

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.sync.BulkUpdaterTest";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();

    // make unique db name to assure we start each test with a clean slate
    dbName = "textExecution" + (new Date()).getTime();
  }
}
