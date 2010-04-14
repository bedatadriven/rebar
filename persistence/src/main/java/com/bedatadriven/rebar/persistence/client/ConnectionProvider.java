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

package com.bedatadriven.rebar.persistence.client;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {


  /**
   * Grab a connection, with the autocommit mode specified by
   * <tt>hibernate.connection.autocommit</tt>.
   * @return a JDBC connection
   * @throws SQLException
   */
  Connection getConnection() throws SQLException;

  /**
   * Dispose of a used connection.
   * @param conn a JDBC connection
   * @throws SQLException
   */
  public void closeConnection(Connection conn) throws SQLException;

  /**
   * Release all resources held by this provider. JavaDoc requires a second sentence.
   */
  public void close();

  
}
