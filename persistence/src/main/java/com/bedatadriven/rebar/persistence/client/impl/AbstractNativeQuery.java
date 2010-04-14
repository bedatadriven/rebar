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

package com.bedatadriven.rebar.persistence.client.impl;


import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Bertram
 */
public abstract class AbstractNativeQuery<T> implements Query {

  protected final Connection conn;

  /**
   * The first row to return
   */
  protected int firstResult = 0;

  /**
   * The maximum number of results to return, or -1
   * if setMaxResults has not been called.
   */
  protected int maxResults = -1;

  protected Map<Integer, Object> positionalParameters = new HashMap<Integer, Object>(0);

  protected Map<String, Object> namedParameters = new HashMap<String, Object>(0);

  public AbstractNativeQuery(Connection conn) {
    this.conn = conn;
  }

  /**
   * @return a prepared statement ready to be executed.
   */
  protected abstract PreparedStatement prepareStatement();

  protected abstract void initColumnMapping(ResultSet rs);

  @Override
  public List<T> getResultList() {
    ResultSet rs = null;
    try {
      List<T> results = new ArrayList<T>();
      PreparedStatement stmt = prepareStatement();
      rs = stmt.executeQuery();
      initColumnMapping(rs);
      while (rs.next()) {
        results.add(newResultInstance(rs));
      }
      rs.close();
      return results;

    } catch (SQLException e) {
      if (rs != null) {
        try {
          rs.close();
        } catch (Throwable ignored) {
        }
      }
      throw new RuntimeException(e);
    }
  }


  @Override
  public T getSingleResult() {
    ResultSet rs = null;
    try {
      T result = null;
      PreparedStatement stmt = prepareStatement();
      rs = stmt.executeQuery();
      if (rs.next()) {
        result = newResultInstance(rs);
      }
      rs.close();
      return result;

    } catch (SQLException e) {
      if (rs != null) {
        try {
          rs.close();
        } catch (Throwable ignored) {
        }
      }
      throw new RuntimeException(e);
    }
  }

  protected abstract T newResultInstance(ResultSet rs) throws SQLException;

  @Override
  public int executeUpdate() {
    try {
      PreparedStatement stmt = prepareStatement();
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public Query setMaxResults(int maxResult) {
    this.maxResults = maxResult;
    return this;
  }

  @Override
  public Query setFirstResult(int startPosition) {
    this.firstResult = startPosition;
    return this;
  }

  @Override
  public Query setHint(String hintName, Object value) {
    return this;
  }

  @Override
  public Query setFlushMode(FlushModeType flushMode) {
    return this;
  }
}
