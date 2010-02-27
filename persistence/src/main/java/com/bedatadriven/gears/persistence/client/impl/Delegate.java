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

package com.bedatadriven.gears.persistence.client.impl;

import javax.persistence.Query;

/**
 * Accepts delegation for the EntityManager for a single Entity Class
 *
 * @see javax.persistence.EntityManager EntityManager
 */
public interface Delegate<T, K> {

  void persist(T entity);

  void remove(T entity);

  void refresh(T entity);

  T merge(T entity);

  T find(K primaryKey);

  T getReference(K primaryKey);

  boolean contains(T entity);

  void clear();

  Query createNativeBoundQuery(String sql);
}
