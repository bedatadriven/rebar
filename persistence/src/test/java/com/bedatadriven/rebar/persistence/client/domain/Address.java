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

import javax.persistence.Embeddable;

/**
 * @author Alex Bertram
 */
@Embeddable
public class Address {

  private String streetAddress1;
  private String streetAddress2;
  private String city;
  private String state;
  private int zip;
  private int zip4;


  public Address() {
  }

  public Address(String streetAddress1, String streetAddress2, String city, String state, int zip, int zip4) {
    this.streetAddress1 = streetAddress1;
    this.streetAddress2 = streetAddress2;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.zip4 = zip4;
  }

  public String getStreetAddress1() {
    return streetAddress1;
  }

  public void setStreetAddress1(String streetAddress1) {
    this.streetAddress1 = streetAddress1;
  }

  public String getStreetAddress2() {
    return streetAddress2;
  }

  public void setStreetAddress2(String streetAddress2) {
    this.streetAddress2 = streetAddress2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public int getZip() {
    return zip;
  }

  public void setZip(int zip) {
    this.zip = zip;
  }

  public int getZip4() {
    return zip4;
  }

  public void setZip4(int zip4) {
    this.zip4 = zip4;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Address address = (Address) o;

    if (zip != address.zip) return false;
    if (zip4 != address.zip4) return false;
    if (city != null ? !city.equals(address.city) : address.city != null) return false;
    if (state != null ? !state.equals(address.state) : address.state != null) return false;
    if (streetAddress1 != null ? !streetAddress1.equals(address.streetAddress1) : address.streetAddress1 != null)
      return false;
    if (streetAddress2 != null ? !streetAddress2.equals(address.streetAddress2) : address.streetAddress2 != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = streetAddress1 != null ? streetAddress1.hashCode() : 0;
    result = 31 * result + (streetAddress2 != null ? streetAddress2.hashCode() : 0);
    result = 31 * result + (city != null ? city.hashCode() : 0);
    result = 31 * result + (state != null ? state.hashCode() : 0);
    result = 31 * result + zip;
    result = 31 * result + zip4;
    return result;
  }

  @Override
  public String toString() {
    return "Address{" +
        "streetAddress1='" + streetAddress1 + '\'' +
        ", streetAddress2='" + streetAddress2 + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zip=" + zip +
        ", zip4=" + zip4 +
        '}';
  }


}
