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

}
