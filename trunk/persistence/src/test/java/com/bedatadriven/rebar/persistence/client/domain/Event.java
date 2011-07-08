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

import javax.persistence.*;
import java.util.Date;

/**
 * @author Alex Bertram
 */
@Entity
public class Event {

  private int id;
  private Integer attendedCount;
  private Date eventDate;

  public Event() {
  }

  public Event(int id, Integer attendedCount, Date eventDate) {
    this.id = id;
    this.attendedCount = attendedCount;
    this.eventDate = eventDate;
  }

  @Id
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Column(nullable = true)
  public Integer getAttendedCount() {
    return attendedCount;
  }

  public void setAttendedCount(Integer attendedCount) {
    this.attendedCount = attendedCount;
  }

  @Column(nullable = true)
  @Temporal(TemporalType.DATE)
  public Date getEventDate() {
    return eventDate;
  }

  public void setEventDate(Date eventDate) {
    this.eventDate = eventDate;
  }
}
