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

package com.bedatadriven.gears.persistence.mapping;

import com.google.gwt.core.ext.typeinfo.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides a common interface to compile-time GWT TypeOracle classes
 * (JClassType) and
 *
 * @author Alex Bertram
 */
public interface TypeInfo {


  String getQualifiedName();

  String getSimpleName();

  String getQualifiedBoxedName();

  <T extends Annotation> T getAnnotation(Class<T> annotation);

  List<MethodInfo> getMethods();

  Class isPrimitive();

  TypeInfo getItemType();


  public static class GwtImpl implements TypeInfo {
    private final JType type;

    public GwtImpl(JType type) {
      this.type = type;
    }

    @Override
    public String getQualifiedName() {
      return type.getQualifiedSourceName();
    }

    @Override
    public String getSimpleName() {
      return type.getSimpleSourceName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
      if (type instanceof HasAnnotations) {
        return ((HasAnnotations) type).getAnnotation(annotation);
      } else {
        return null;
      }
    }

    @Override
    public List<MethodInfo> getMethods() {
      List<MethodInfo> list = new ArrayList<MethodInfo>();
      JClassType classType = type.isClass();
      if (classType != null) {
        for (JMethod method : classType.getMethods()) {
          list.add(new MethodInfo.GwtImpl(method));
        }
      }
      return list;
    }

    @Override
    public String getQualifiedBoxedName() {
      JPrimitiveType p = type.isPrimitive();
      if (p != null) {
        return p.getQualifiedBoxedSourceName();
      } else {
        return getQualifiedName();
      }
    }

    @Override
    public Class isPrimitive() {
      JPrimitiveType p = type.isPrimitive();
      if (p == null)
        return null;
      if (p == JPrimitiveType.INT) return Integer.TYPE;
      if (p == JPrimitiveType.BYTE) return Byte.TYPE;
      if (p == JPrimitiveType.LONG) return Long.TYPE;
      if (p == JPrimitiveType.SHORT) return Short.TYPE;
      if (p == JPrimitiveType.CHAR) return Character.TYPE;
      if (p == JPrimitiveType.FLOAT) return Float.TYPE;
      if (p == JPrimitiveType.DOUBLE) return Double.TYPE;
      if (p == JPrimitiveType.BOOLEAN) return Boolean.TYPE;

      throw new Error("Wasn't expecting something other than one of the 8 primitives.");
    }

    @Override
    public TypeInfo getItemType() {
      JClassType c = type.isClassOrInterface();
      if (c == null)
        return null;
      for (JClassType i : c.getImplementedInterfaces()) {
        if (i.getQualifiedSourceName() == Collection.class.getName()) {
          JParameterizedType pt = i.isParameterized();
          if (pt == null) return null;
          if (pt.getTypeArgs().length == 0) return null;
          return new GwtImpl(pt.getTypeArgs()[0]);
        }
      }
      return null;
    }
  }

  public static class RuntimeImpl implements TypeInfo {
    private final Class myClass;

    public RuntimeImpl(Class myClass) {
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
        list.add(new MethodInfo.RuntimeImpl(method));
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
          return new RuntimeImpl(i.getTypeParameters()[0].getClass());
        }
      }
      return null;
    }
  }

}
