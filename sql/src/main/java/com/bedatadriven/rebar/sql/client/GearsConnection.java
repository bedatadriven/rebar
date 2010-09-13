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

import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;

import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * @author Alex Bertram
 */
class GearsConnection implements Connection {

  private Database database;
  private boolean closed = true;
  private boolean autoCommit = true;
  private int nextSavepointId = 1;

  private int transactionIsolation = TRANSACTION_SERIALIZABLE;


  /**
   * @param database An open Gears <code>Database</code>
   */
  GearsConnection(Database database) {
    this.database = database;
    this.closed = false;
  }

  public Statement createStatement() throws SQLException {
    return new GearsStatement(this, database);
  }

  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return new GearsPreparedStatement(this, database, sql);
  }


  public void close() throws SQLException {
    try {
      database.close();
      database = null;
    } catch (DatabaseException e) {
      throw new SQLException(e);
    }
    closed = true;
  }

  public boolean isClosed() throws SQLException {
    return closed;
  }

  public DatabaseMetaData getMetaData() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public void setReadOnly(boolean readOnly) throws SQLException {
    if (readOnly)
      throw new IllegalArgumentException();
  }

  public boolean isReadOnly() throws SQLException {
    return true;
  }

  public void setCatalog(String catalog) throws SQLException {
    // silently ignoring, according to spec
  }

  public String getCatalog() throws SQLException {
    // returning null, according to spec
    return null;
  }

  public int getHoldability() throws SQLException {
    return ResultSet.CLOSE_CURSORS_AT_COMMIT;
  }

  public void setHoldability(int h) throws SQLException {
    if (h != ResultSet.CLOSE_CURSORS_AT_COMMIT)
      throw new SQLException("SQLite only supports CLOSE_CURSORS_AT_COMMIT");
  }

  public int getTransactionIsolation() {
    return transactionIsolation;
  }

  public void setTransactionIsolation(int level) throws SQLException {
    switch (level)
    {
      case TRANSACTION_SERIALIZABLE:
        execute("PRAGMA read_uncommitted = false;");
        break;
      case TRANSACTION_READ_UNCOMMITTED:
        execute("PRAGMA read_uncommitted = true;");
        break;
      default:
        throw new SQLException("SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED.");
    }
    transactionIsolation = level;
  }

  private void execute(String sql) throws SQLException {
    try {
      database.execute(sql);
    } catch (DatabaseException e) {
      throw new SQLException(e.getMessage(), e);
    }
  }


  public SQLWarning getWarnings() throws SQLException {
    return null;
  }


  public void clearWarnings() throws SQLException {

  }


  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    if (resultSetType != ResultSet.TYPE_FORWARD_ONLY) {
      throw new SQLFeatureNotSupportedException("The Gears database supports only TYPE_FORWARD_ONLY recordsets.");
    }
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY)
      throw new SQLFeatureNotSupportedException("The Gears database supports only CONCUR_READ_ONLY");

    return new GearsStatement(this, database);
  }


  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    if (resultSetType != ResultSet.TYPE_FORWARD_ONLY) {
      throw new SQLFeatureNotSupportedException("The Gears database supports only TYPE_FORWARD_ONLY recordsets.");
    }
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY)
      throw new SQLFeatureNotSupportedException("The Gears database supports only CONCUR_READ_ONLY");

    return new GearsPreparedStatement(this, database, sql);
  }


  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return Collections.emptyMap();
  }


  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    if (resultSetType != ResultSet.TYPE_FORWARD_ONLY) {
      throw new SQLFeatureNotSupportedException("The Gears database supports only TYPE_FORWARD_ONLY recordsets.");
    }
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY)
      throw new SQLFeatureNotSupportedException("The Gears database supports only CONCUR_READ_ONLY");

    return new GearsStatement(this, database);
  }


  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    if (resultSetType != ResultSet.TYPE_FORWARD_ONLY) {
      throw new SQLFeatureNotSupportedException("The Gears database supports only TYPE_FORWARD_ONLY recordsets.");
    }
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY)
      throw new SQLFeatureNotSupportedException("The Gears database supports only CONCUR_READ_ONLY");

    return new GearsPreparedStatement(this, database, sql);
  }


  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }


  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return new GearsPreparedStatement(this, database, sql);
  }


  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return new GearsPreparedStatement(this, database, sql);
  }

  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return new GearsPreparedStatement(this, database, sql);
  }

  public Clob createClob() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public Blob createBlob() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public NClob createNClob() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public SQLXML createSQLXML() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public boolean isValid(int timeout) throws SQLException {
    try {
      database.execute("select * from sqlite_master");
      return true;
    } catch (DatabaseException e) {
      return false;
    }
  }

  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    throw new UnsupportedOperationException();
  }

  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    throw new UnsupportedOperationException();
  }

  public String getClientInfo(String name) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public Properties getClientInfo() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
    if(iface == Database.class)
      return (T) database;   
    throw new SQLException("not a wrapper for " + iface.getName());
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    if(iface == Database.class)
      return true;
    return false;
  }

  public CallableStatement prepareCall(String sql) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  public String nativeSQL(String sql) throws SQLException {
    return sql;
  }

  public boolean getAutoCommit() throws SQLException
  {
    return autoCommit;
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException
  {
    if (this.autoCommit != autoCommit) {
      this.autoCommit = autoCommit;
      execute(autoCommit ? "commit" : "begin");
    }
  }

  private void checkAutoCommitOff() throws SQLException {
    if (autoCommit)
      throw new SQLException("database in auto-commit mode");
  }

  public void commit() throws SQLException
  {
    checkAutoCommitOff();
    execute("commit");
    execute("begin");
  }

  public void rollback() throws SQLException
  {
    checkAutoCommitOff();
    execute("rollback");
    execute("begin");
  }

  public Savepoint setSavepoint() throws SQLException {
    SqliteSavepoint savepoint = new SqliteSavepoint(nextSavepointId++, null);
    execute("savepoint sp" + savepoint.getSavepointId());
    return savepoint;
  }

  public Savepoint setSavepoint(String name) throws SQLException {
    SqliteSavepoint savepoint = new SqliteSavepoint(nextSavepointId++, name);
    execute("savepoint sp" + savepoint.getSavepointId());
    return savepoint;
  }

  public void rollback(Savepoint savepoint) throws SQLException {
    execute("rollback transaction to savepoint sp" + savepoint.getSavepointId());
  }

  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    execute("release savepoint sp" + savepoint.getSavepointId());
  }
}
