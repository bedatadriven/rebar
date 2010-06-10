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
