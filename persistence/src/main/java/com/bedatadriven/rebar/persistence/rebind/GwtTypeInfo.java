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

package com.bedatadriven.rebar.persistence.rebind;

import com.bedatadriven.rebar.persistence.mapping.MethodInfo;
import com.bedatadriven.rebar.persistence.mapping.TypeInfo;
import com.google.gwt.core.ext.typeinfo.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GwtTypeInfo implements TypeInfo {
  private final JType type;

  public GwtTypeInfo(JType type) {
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
        list.add(new GwtMethodInfo(method));
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
        return new GwtTypeInfo(pt.getTypeArgs()[0]);
      }
    }
    return null;
  }
}
