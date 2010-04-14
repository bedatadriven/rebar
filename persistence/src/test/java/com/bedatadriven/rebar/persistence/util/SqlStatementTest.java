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

package com.bedatadriven.rebar.persistence.util;

import com.bedatadriven.rebar.persistence.util.SQLStatement;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alex Bertram
 */
public class SqlStatementTest {

  @Test
  public void testSimple() {

    SQLStatement sql = new SQLStatement("  selEcT a,b from   mytable where x is not null  ");

    Assert.assertEquals("token count", 11, sql.getTokens().size());
    Assert.assertEquals("mytable", sql.getTokens().get(5).toString());
    Assert.assertEquals("null", sql.getTokens().get(10).toString());
    Assert.assertEquals("query type", SQLStatement.TYPE_SELECT, sql.getQueryType());
  }

  @Test
  public void testParams() {
    SQLStatement sql = new SQLStatement("SELECT * from mytable where a = ?1 or b= :keyId");
    Assert.assertEquals(1, sql.getTokens().get(7).getParameterPosition());
    Assert.assertEquals("keyId", sql.getTokens().get(11).getParameterName());
  }

  @Test
  public void testSymbols() {
    //                                   |      | |    |       |     || | |   || | |   |||
    SQLStatement sql = new SQLStatement("SELECT * from mytable where a<>b and a<=b and c=d");

    for (int i = 0; i != sql.getTokens().size(); ++i) {
      System.out.println(sql.getTokens().get(i).toString());
    }

    Assert.assertEquals(16, sql.getTokens().size());

  }
}
