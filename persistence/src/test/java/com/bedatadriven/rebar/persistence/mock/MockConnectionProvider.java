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

package com.bedatadriven.rebar.persistence.mock;

import com.bedatadriven.rebar.persistence.client.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Alex Bertram
 */
public class MockConnectionProvider implements ConnectionProvider {


  private String name;
  private Connection connection = null;

  public MockConnectionProvider() {
    name = ":memory:";
  }

  public MockConnectionProvider(String name) {
    this.name = name;
  }

  public Connection getConnection() throws SQLException {
    if(connection == null) {
      try {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + name);
      } catch (Exception e) {
        throw new Error(e);
      }
    }
    return connection;
  }

  public void closeConnection(Connection conn) throws SQLException {

  }

  public void close() {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
