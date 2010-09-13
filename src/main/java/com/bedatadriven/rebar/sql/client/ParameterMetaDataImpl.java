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

package com.bedatadriven.rebar.sql.client;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * @author Alex Bertram
 */
class ParameterMetaDataImpl implements ParameterMetaData {

  private Statement statement;
  private int count;

  ParameterMetaDataImpl(Statement statement, String sql) {
    this.statement = statement;
    this.count = countPlaceholders(sql);
  }

  private int countPlaceholders(String sql) {
    int count = 0;
    int i = sql.indexOf('?');
    while(i!=-1) {
      count++;
      i = sql.indexOf('?', i+1);
    }
    return count;
  }

  public ParameterMetaData getParameterMetaData()
  {
      return this;
  }

  public int getParameterCount() throws SQLException
  {
      return count;
  }

  public String getParameterClassName(int param) throws SQLException
  {
      return "java.lang.String";
  }

  public String getParameterTypeName(int pos)
  {
      return "VARCHAR";
  }

  public int getParameterType(int pos)
  {
      return Types.VARCHAR;
  }

  public int getParameterMode(int pos)
  {
      return parameterModeIn;
  }

  public int getPrecision(int pos)
  {
      return 0;
  }

  public int getScale(int pos)
  {
      return 0;
  }

  public int isNullable(int pos)
  {
      return parameterNullable;
  }

  public boolean isSigned(int pos)
  {
      return true;
  }

  public Statement getStatement()
  {
      return statement;
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Not a wrapper");
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
