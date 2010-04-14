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
import java.util.List;
import java.util.Collection;
import java.util.ListIterator;

/**
 * @author Alex Bertram
 */
public class PersistentList<T,K> extends AbstractPersistentCollection<T,K>
    implements List<T> {

  List<T> inner;

  public PersistentList(Delegate<T, K> tkDelegate, Query query) {
    super(tkDelegate, query);
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    assureLoaded();
    return inner.addAll(index,c);
  }

  public T get(int index) {
    assureLoaded();
    return inner.get(index);
  }

  public T set(int index, T element) {
    assureLoaded();
    return inner.set(index, element);
  }

  public void add(int index, T element) {
    assureLoaded();
    inner.add(index,element);
  }

  public T remove(int index) {
    assureLoaded();
    return inner.remove(index);
  }

  public int indexOf(Object o) {
    assureLoaded();
    return inner.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    assureLoaded();
    return inner.lastIndexOf(o);
  }

  public ListIterator<T> listIterator() {
    assureLoaded();
    return inner.listIterator();
  }

  public ListIterator<T> listIterator(int index) {
    assureLoaded();
    return inner.listIterator(index);
  }

  public List<T> subList(int fromIndex, int toIndex) {
    assureLoaded();
    return inner.subList(fromIndex, toIndex);
  }
}
