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

import javax.persistence.Embeddable;

/**
 * @author Alex Bertram
 */
@Embeddable
public class Bounds {

  private Double x1;
  private Double y1;
  private Double x2;
  private Double y2;

  public Bounds() {
  }

  public Bounds(Double x1, Double y1, Double x2, Double y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public Double getX1() {
    return x1;
  }

  public void setX1(Double x1) {
    this.x1 = x1;
  }

  public Double getY1() {
    return y1;
  }

  public void setY1(Double y1) {
    this.y1 = y1;
  }

  public Double getX2() {
    return x2;
  }

  public void setX2(Double x2) {
    this.x2 = x2;
  }

  public Double getY2() {
    return y2;
  }

  public void setY2(Double y2) {
    this.y2 = y2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Bounds bounds = (Bounds) o;

    if (!x1.equals(bounds.x1)) return false;
    if (!x2.equals(bounds.x2)) return false;
    if (!y1.equals(bounds.y1)) return false;
    if (!y2.equals(bounds.y2)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = x1.hashCode();
    result = 31 * result + y1.hashCode();
    result = 31 * result + x2.hashCode();
    result = 31 * result + y2.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Bounds{" +
        "x1=" + x1 +
        ", y1=" + y1 +
        ", x2=" + x2 +
        ", y2=" + y2 +
        '}';
  }
}
