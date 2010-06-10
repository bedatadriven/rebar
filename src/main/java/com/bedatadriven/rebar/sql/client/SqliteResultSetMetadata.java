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

import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * @author Alex Bertram
 */
class SqliteResultSetMetadata implements ResultSetMetaData {

  private ResultSet rs;

  public SqliteResultSetMetadata(ResultSet rs) {
    this.rs = rs;
  }


  public int getColumnCount() throws SQLException {
    return rs.getFieldCount();
  }


  public boolean isAutoIncrement(int column) throws SQLException {
    // todo
    throw new UnsupportedOperationException();
  }


  public boolean isCaseSensitive(int column) throws SQLException {
    return false;
  }


  public boolean isSearchable(int column) throws SQLException {
    return true;
  }


  public boolean isCurrency(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public int isNullable(int column) throws SQLException {
    return columnNullableUnknown;
  }


  public boolean isSigned(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public int getColumnDisplaySize(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public String getColumnLabel(int column) throws SQLException {
    return getColumnName(column);
  }


  public String getColumnName(int column) throws SQLException {
    try {
      return rs.getFieldName(column - 1);
    } catch (DatabaseException e) {
      throw new SQLException(e);
    }
  }


  public String getSchemaName(int column) throws SQLException {
    return "";
  }


  public int getPrecision(int column) throws SQLException {
    return 0;
  }


  public int getScale(int column) throws SQLException {
    return 0;
  }


  public String getTableName(int column) throws SQLException {
    return "";
  }


  public String getCatalogName(int column) throws SQLException {
    return "";
  }


  public int getColumnType(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public String getColumnTypeName(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public boolean isReadOnly(int column) throws SQLException {
    return false;
  }


  public boolean isWritable(int column) throws SQLException {
    return false;
  }


  public boolean isDefinitelyWritable(int column) throws SQLException {
    return false;
  }


  public String getColumnClassName(int column) throws SQLException {
    throw new UnsupportedOperationException();
  }


  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }


  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
