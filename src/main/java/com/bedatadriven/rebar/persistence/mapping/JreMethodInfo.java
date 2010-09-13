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
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class JreMethodInfo implements MethodInfo {
  private Method method;

  public JreMethodInfo(Method method) {
    this.method = method;
  }

  @Override
  public String getName() {
    return method.getName();
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotation) {
    return method.getAnnotation(annotation);
  }

  @Override
  public int getParameterCount() {
    return method.getParameterTypes().length;
  }

  @Override
  public TypeInfo getReturnType() {
    return new JreTypeInfo(method.getReturnType());
  }

  @Override
  public boolean isPublic() {
    return Modifier.isPublic(method.getModifiers());
  }

  @Override
  public boolean isStatic() {
    return Modifier.isStatic(method.getModifiers());
  }

  public boolean isFinal() {
    return Modifier.isFinal(method.getModifiers());
  }

  @Override
  public List<String> getParameterTypeNames() {
    List<String> types = new ArrayList<String>();
    for (Type t : method.getGenericParameterTypes()) {
      types.add(composeTypeDeclaration(t));
    }
    return types;
  }

  private String composeTypeDeclaration(Type type) {
    if (type instanceof Class) {

      Class clazz = (Class) type;
      StringBuilder sb = new StringBuilder();
      if (clazz.getEnclosingClass() != null) {
        sb.append(composeTypeDeclaration(clazz));
        sb.append(".");
      } else if (clazz.getPackage() != null) {
        sb.append(clazz.getPackage().getName());
        sb.append(".");
      }
      sb.append(clazz.getSimpleName());
      return sb.toString();

    } else if (type instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) type;
      return composeTypeDeclaration(arrayType.getGenericComponentType()) + "[]";

    } else if (type instanceof TypeVariable) {
      TypeVariable typeVar = (TypeVariable) type;
      return typeVar.getName();

    } else if (type instanceof WildcardType) {
      WildcardType wildcard = (WildcardType) type;
      StringBuilder sb = new StringBuilder("?");
      for (int i = 0; i != wildcard.getLowerBounds().length; ++i) {
        sb.append(i == 0 ? " super " : ", ");
        sb.append(composeTypeDeclaration(wildcard.getLowerBounds()[i]));
      }
      for (int i = 0; i != wildcard.getUpperBounds().length; ++i) {
        sb.append(i == 0 ? " extends " : ", ");
        sb.append(composeTypeDeclaration(wildcard.getUpperBounds()[i]));
      }
      return sb.toString();

    } else if (type instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType) type;
      StringBuilder sb = new StringBuilder();
      sb.append(composeTypeDeclaration(ptype.getRawType()));
      sb.append("<");
      for (int i = 0; i != ptype.getActualTypeArguments().length; ++i) {
        if (i != 0) sb.append(", ");
        sb.append(composeTypeDeclaration(ptype.getActualTypeArguments()[i]));
      }
      sb.append(">");
      return sb.toString();
    } else {
      throw new Error("Unexpected subclass" + type.getClass().getName());
    }
  }
}
