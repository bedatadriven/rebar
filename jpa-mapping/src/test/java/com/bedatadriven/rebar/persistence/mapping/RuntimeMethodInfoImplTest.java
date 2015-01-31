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

package com.bedatadriven.rebar.persistence.mapping;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Bertram
 */
public class RuntimeMethodInfoImplTest {

  @Test
  public void testComposeTypeDecl() {

    TypeInfo typeInfo = new JreTypeInfo(MyClass.class);
    MethodInfo method = typeInfo.getMethods().get(0);
    List<String> params = method.getParameterTypeNames();

    Assert.assertEquals("java.lang.String", params.get(0));
    Assert.assertEquals("java.util.List<java.util.Set<java.lang.Integer>>", params.get(1));
    Assert.assertEquals("java.util.Map<java.lang.Integer, java.lang.String>[]", params.get(2));
    Assert.assertEquals("java.util.Collection<? extends java.lang.Integer>", params.get(3));
    Assert.assertEquals("Z", params.get(4));
    Assert.assertEquals("boolean", params.get(5));
    Assert.assertEquals("int[]", params.get(6));
  }

  public class MyClass<Z> {

    public void aMethod(String p0,
                        List<Set<Integer>> p1,
                        Map<Integer, String>[] p2,
                        Collection<? extends Integer> p3,
                        Z p4,
                        boolean p5,
                        int[] p6) {

    }
  }
}
