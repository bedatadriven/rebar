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

package com.bedatadriven.gears.persistence.client.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Alex Bertram
 */
@Entity
public class Province {

  private String code;
  private String name;
  private Bounds bounds;
  private Integer population;

  public Province() {
  }

  public Province(String code, String name, Bounds bounds, Integer population) {
    this.code = code;
    this.name = name;
    this.bounds = bounds;
    this.population = population;
  }

  @Id
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Embedded
  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  public Integer getPopulation() {
    return population;
  }

  public void setPopulation(Integer population) {
    this.population = population;
  }

  public boolean isLargerThan(Province otherProvince) {
    return getPopulation() > otherProvince.getPopulation();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Province province = (Province) o;

    if (bounds != null ? !bounds.equals(province.bounds) : province.bounds != null) return false;
    if (!code.equals(province.code)) return false;
    if (!name.equals(province.name)) return false;
    if (population != null ? !population.equals(province.population) : province.population != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = code.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + (bounds != null ? bounds.hashCode() : 0);
    result = 31 * result + (population != null ? population.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Province{" +
        "code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", bounds=" + bounds +
        ", population=" + population +
        '}';
  }
}
