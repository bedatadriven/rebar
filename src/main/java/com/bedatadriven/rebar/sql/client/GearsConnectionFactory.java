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

import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class for creating java.sql.Connection for local (client-side) databases.
 *
 *
 * @author Alex Bertram
 */
public class GearsConnectionFactory {

  /**
   * @param databaseName
   * @return
   * @throws SQLException
   */
  public static Connection getConnection(String databaseName) throws SQLException {

    Factory factory = Factory.getInstance();
    if (factory == null)
      throw new SQLException("Factory instance is null");

    GWT.log("DriverManager: Creating connection for database '" + databaseName + "'", null);

    Database db = factory.createDatabase();
    db.open(databaseName);

    return new GearsConnection(db);

  }

  public static Connection getConnection() throws SQLException {

    Factory factory = Factory.getInstance();
    if (factory == null)
      throw new SQLException("Factory instance is null");

    Database db = factory.createDatabase();
    db.open();

    return new GearsConnection(db);

  }
}
