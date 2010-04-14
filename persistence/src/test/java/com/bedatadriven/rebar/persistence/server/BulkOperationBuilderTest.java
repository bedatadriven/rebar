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

package com.bedatadriven.rebar.persistence.server;

import com.bedatadriven.rebar.persistence.client.domain.Simple;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Bertram
 */
public class BulkOperationBuilderTest {

  /**
   * Assures that an insert statement executes successfullyu
   *
   * @throws JSONException
   */
  @Test
  public void testInsert() throws JSONException {

    List<Simple> list = new ArrayList<Simple>();
    list.add(new Simple(1, "Hello World", true));
    list.add(new Simple(3, null, false));
    list.add(new Simple(9, "Here we are", false));

    BulkOperationBuilder builder = new BulkOperationBuilder();
    builder.insert(Simple.class, list);

    String json = builder.asJson();

    System.out.println(json);
  }
}
