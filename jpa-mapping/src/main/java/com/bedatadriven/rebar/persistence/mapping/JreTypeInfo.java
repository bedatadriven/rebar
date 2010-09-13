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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JreTypeInfo implements TypeInfo {
  private final Class myClass;

  public JreTypeInfo(Class myClass) {
    this.myClass = myClass;
  }

  @Override
  public String getQualifiedName() {
    return myClass.getName();
  }

  @Override
  public String getSimpleName() {
    return myClass.getSimpleName();
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotation) {
    return (T) myClass.getAnnotation(annotation);
  }

  @Override
  public List<MethodInfo> getMethods() {
    List<MethodInfo> list = new ArrayList<MethodInfo>(myClass.getMethods().length);
    for (Method method : myClass.getMethods()) {
      list.add(new JreMethodInfo(method));
    }
    return list;
  }

  @Override
  public Class isPrimitive() {
    if (!myClass.isPrimitive())
      return null;

    return myClass;
  }

  @Override
  public String getQualifiedBoxedName() {
    if (myClass == Integer.TYPE) return Integer.class.getName();
    if (myClass == Long.TYPE) return Long.class.getName();
    if (myClass == Short.TYPE) return Short.class.getName();
    if (myClass == Character.TYPE) return Character.class.getName();
    if (myClass == Float.TYPE) return Float.class.getName();
    if (myClass == Double.TYPE) return Double.class.getName();
    if (myClass == Boolean.TYPE) return Boolean.class.getName();
    return myClass.getName();
  }

  @Override
  public TypeInfo getItemType() {
    for (Class i : myClass.getInterfaces()) {
      if (i == Collection.class) {
        if (i.getTypeParameters().length == 0) return null;
        return new JreTypeInfo(i.getTypeParameters()[0].getClass());
      }
    }
    return null;
  }
}
