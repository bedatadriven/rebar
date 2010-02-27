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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Alex Bertram
 */
public class Readers {

  public static String readString(ResultSet rs, int index) throws SQLException {
    return rs.getString(index);
  }

  public static Boolean readBoolean(ResultSet rs, int index) throws SQLException {
    boolean value = rs.getBoolean(index);
    return rs.wasNull() ? null : value;
  }

  public static Double readDouble(ResultSet rs, int index) throws SQLException {
    double value = rs.getDouble(index);
    return rs.wasNull() ? null : value;
  }

  public static Float readFloat(ResultSet rs, int index) throws SQLException {
    float value = rs.getFloat(index);
    return rs.wasNull() ? null : value;
  }

  public static Integer readInt(ResultSet rs, int index) throws SQLException {
    int value = rs.getInt(index);
    return rs.wasNull() ? null : value;
  }

  public static Short readShort(ResultSet rs, int index) throws SQLException {
    short value = rs.getShort(index);
    return rs.wasNull() ? null : value;
  }

  public static Date readDate(ResultSet rs, int index) throws SQLException {
    java.sql.Date date = rs.getDate(index);
    return rs.wasNull() ? null : new Date(date.getTime());
  }

  public static java.sql.Date toSqlDate(Date date) {
    return date == null ? null :  new java.sql.Date(date.getTime());
  }


}
