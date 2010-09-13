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

package com.bedatadriven.rebar.persistence.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Alex Bertram
 */
@Entity
public class Simple {

  private int id;
  private String name;
  private boolean available;

  public Simple() {
  }

  public Simple(int id, String name, boolean available) {
    this.id = id;
    this.name = name;
    this.available = available;
  }

  @Id
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="available_flag")   // just to be difficult
  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Simple)) return false;

    Simple simple = (Simple) o;

    if (available != simple.available) return false;
    if (id != simple.id) return false;
    if (name != null ? !name.equals(simple.name) : simple.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (available ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Simple{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", available=" + available +
        '}';
  }
}
