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

package com.bedatadriven.rebar.persistence.client.impl;

import javax.persistence.Query;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Alex Bertram
 */
public class AbstractPersistentCollection<T, K> implements Collection<T> {

  private final Delegate<T, K> delegate;
  private final Query query;

  /**
   * The actual set, loaded on demand.
   */
  private Collection<T> collection = null;

  public AbstractPersistentCollection(Delegate<T, K> delegate, Query query) {
    this.delegate = delegate;
    this.query = query;
  }

  protected void assureLoaded() {
    if (collection == null) {
      collection = new HashSet<T>(
          query.getResultList());
    }
  }


  @Override
  public int size() {
    assureLoaded();
    return collection.size();
  }

  @Override
  public boolean isEmpty() {
    assureLoaded();
    return collection.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    assureLoaded();
    return collection.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    assureLoaded();
    return collection.iterator();
  }

  @Override
  public Object[] toArray() {
    assureLoaded();
    return collection.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    assureLoaded();
    return collection.toArray(a);
  }

  @Override
  public boolean add(T t) {
    assureLoaded();
    return collection.add(t);
  }

  @Override
  public boolean remove(Object o) {
    assureLoaded();
    return collection.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    assureLoaded();
    return collection.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    assureLoaded();
    return collection.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    assureLoaded();
    return collection.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    assureLoaded();
    return collection.removeAll(c);
  }

  @Override
  public void clear() {
    assureLoaded();
    collection.clear();
  }


}
