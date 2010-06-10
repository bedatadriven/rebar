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
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class GwtMethodInfo implements MethodInfo {
  private JMethod method;

  public GwtMethodInfo(JMethod method) {
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
    return method.getParameters().length;
  }

  @Override
  public TypeInfo getReturnType() {
    return new GwtTypeInfo(method.getReturnType());
  }

  @Override
  public boolean isPublic() {
    return method.isPublic();
  }

  @Override
  public boolean isStatic() {
    return method.isStatic();
  }

  public boolean isFinal() {
    return method.isFinal();
  }

  @Override
  public List<String> getParameterTypeNames() {
    List<String> types = new ArrayList<String>();
    for (JParameter t : method.getParameters()) {
      types.add(t.getType().getParameterizedQualifiedSourceName());
    }
    return types;
  }
}
