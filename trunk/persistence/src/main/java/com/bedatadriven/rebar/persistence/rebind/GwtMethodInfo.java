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
