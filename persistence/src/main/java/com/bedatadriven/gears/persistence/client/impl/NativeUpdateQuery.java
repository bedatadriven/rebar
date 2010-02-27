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

import javax.persistence.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * @author Alex Bertram
 */
public class NativeUpdateQuery extends AbstractNativeRuntimeQuery {

  private EntityManager em;
  private PreparedStatement stmt;

  public NativeUpdateQuery(EntityManager em, Connection conn, String sql) {
    super(conn, sql);
    this.em = em;
  }

  public List getResultList() {
    throw new PersistenceException("getResultList() cannot be called for update queries");
  }

  public Object getSingleResult() {
    throw new PersistenceException("getSingleResult() cannot be called for update queries");
  }

  public int executeUpdate() {
    try {
      if(stmt == null)
        stmt = prepareStatement();
      else
        fillParameters(stmt);

      return stmt.executeUpdate();

    } catch (SQLException e) {
      throw new PersistenceException(e);
    }
  }

  @Override
  protected void initColumnMapping(ResultSet rs) {

  }

  @Override
  protected Object newResultInstance(ResultSet rs) throws SQLException {
    return null;
  }
}
