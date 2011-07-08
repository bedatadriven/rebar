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

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Alex Bertram
 */
@Entity
public class Person {

  private int id;
  private String name;
  private Date birthDate;
  private double height;
  private boolean active;

  public Person() {
  }

  public Person(int id, String name, Date birthDate, double height, boolean active) {
    this.id = id;
    this.name = name;
    this.birthDate = birthDate;
    this.height = height;
    this.active = active;
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

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;

    Person person = (Person) o;

    if (active != person.active) return false;
    if (Double.compare(person.height, height) != 0) return false;
    if (id != person.id) return false;
    if (birthDate != null ? !birthDate.equals(person.birthDate) : person.birthDate != null) return false;
    if (name != null ? !name.equals(person.name) : person.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
    result = 31 * result + (active ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Person{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", birthDate=" + birthDate +
        ", height=" + height +
        ", active=" + active +
        '}';
  }
}
