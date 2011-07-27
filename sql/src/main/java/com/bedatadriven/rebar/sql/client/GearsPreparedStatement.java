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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Bertram
 */
class GearsPreparedStatement extends GearsStatement implements PreparedStatement {

	private JavaScriptObject parameters = createParamArray();
  private int numParameters = 0;

  public GearsPreparedStatement(GearsConnection connection, Database database) {
    super(connection, database);
  }

  public GearsPreparedStatement(GearsConnection connection, Database database, String sql) {
    super(connection, database, sql);
  }

  private static native JavaScriptObject createParamArray() /*-{
  	return [];
  }-*/;

  // IMPORTANT: note the explicit call to String() in the js code. Without this we get
  // a js String object that gears chokes on
  // c.f. http://code.google.com/p/google-web-toolkit/issues/detail?id=4301
  
  public native void setString(int parameterIndex, String x) throws SQLException /*-{
    this.@com.bedatadriven.rebar.sql.client.GearsPreparedStatement::parameters[parameterIndex-1] = String(x);
  }-*/;

  @Override
  protected com.google.gwt.gears.client.database.ResultSet doExecute() throws DatabaseException {
    Log.debug("Executing prepared statement: " + sql);
    return doJsExecute(database, sql);
  }
  
  private native com.google.gwt.gears.client.database.ResultSet doJsExecute(Database db, String sql) /*-{
  	return db.execute(sql, this.@com.bedatadriven.rebar.sql.client.GearsPreparedStatement::parameters);
  }-*/;


  public ParameterMetaData getParameterMetaData() throws SQLException {
    return new ParameterMetaDataImpl(this, sql);
  }
  
  private native static String paramsToString() /*-{
  	return this.@com.bedatadriven.rebar.sql.client.GearsPreparedStatement::parameters.join(', ');
  }-*/;

  @Override
  protected SQLException makeSqlException(DatabaseException e) {
    try {
      // try to assemble the parameters:
      StringBuilder sb = new StringBuilder();
      sb.append("Gears/Sqlite has thrown an exception on the a PreparedStateent with the following SQL: \n").append(sql);
      sb.append("\nParameters: ");
      sb.append(paramsToString());
      return new SQLException(sb.toString(), e);
    } catch (Throwable caught) {
      return new SQLException("Gears/sqlite has thrown an exception on the SQL: \n" + sql + " and threw " +
          "antoher exception when I tried to list the parameters", e);
    }

  }

  /*
  *
  * Everything below makes reference to the base class or the
  * methods above.
  *
  */

  public ResultSet executeQuery() throws SQLException {
    return doExecuteQuery();
  }

  public int executeUpdate() throws SQLException {
    return doExecuteUpdate();
  }

  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    setString(parameterIndex, null);
  }

  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    setString(parameterIndex, x ? "1" : "0");
  }

  public void setByte(int parameterIndex, byte x) throws SQLException {
    setString(parameterIndex, Byte.toString(x));
  }

  public void setShort(int parameterIndex, short x) throws SQLException {
    setString(parameterIndex, Short.toString(x));
  }

  public void setInt(int parameterIndex, int x) throws SQLException {
    setString(parameterIndex, Integer.toString(x));
  }

  public void setLong(int parameterIndex, long x) throws SQLException {
    setString(parameterIndex, Long.toString(x));
  }

  public void setFloat(int parameterIndex, float x) throws SQLException {
    setString(parameterIndex, Float.toString(x));
  }

  public void setDouble(int parameterIndex, double x) throws SQLException {
    setString(parameterIndex, Double.toString(x));
  }

  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    // TODO
    throw new SQLFeatureNotSupportedException();
  }

  public void setDate(int parameterIndex, Date x) throws SQLException {
    // TODO: is this correct?
    setString(parameterIndex, x == null ? null : Long.toString(x.getTime()));
  }

  public void setTime(int parameterIndex, Time x) throws SQLException {
    setString(parameterIndex, x == null ? null : x.toString());
  }

  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    setString(parameterIndex, x == null ? null : x.toString());
  }

  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Deprecated
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void clearParameters() throws SQLException {
    parameters = createParamArray();
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    setObject(parameterIndex, x);
  }

  public void setObject(int parameterIndex, Object x) throws SQLException {
    if(x == null) {
    	setString(parameterIndex, null);
    } else if (x instanceof Date) {
      setDate(parameterIndex, (Date) x);
    } else {
      setString(parameterIndex, x.toString());
    }
  }

  public boolean execute() throws SQLException {
    throw new SQLFeatureNotSupportedException("You must call executeQuery or executeUpdate for the time being.");
  }

  public void addBatch() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setRef(int parameterIndex, Ref x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setClob(int parameterIndex, Clob x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setArray(int parameterIndex, Array x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public ResultSetMetaData getMetaData() throws SQLException {
    return null;
  }

  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    setDate(parameterIndex, x);
  }

  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    setTime(parameterIndex, x);
  }

  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    setTimestamp(parameterIndex, x);
  }

  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    setString(parameterIndex, null);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    setObject(parameterIndex, x);
  }

  /*
  * Unimplemented parameter setters below
  */

  public void setURL(int parameterIndex, URL x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setNString(int parameterIndex, String value) throws SQLException {
    setString(parameterIndex, value);
  }

  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
}
