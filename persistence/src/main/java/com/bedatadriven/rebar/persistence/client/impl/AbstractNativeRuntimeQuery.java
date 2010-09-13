

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

import com.bedatadriven.rebar.persistence.util.SQLStatement;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the query interface for queries that are known only at run time.
 *
 * @author Alex Bertram
 */
public abstract class AbstractNativeRuntimeQuery<T, K> extends AbstractNativeQuery {

  private final SQLStatement sql;
  private Map<String, Object> namedParameters = new HashMap<String, Object>();
  private Map<Integer, Object> posParameters = new HashMap<Integer, Object>();

  public AbstractNativeRuntimeQuery(Connection conn, String sql) {
    super(conn);
    this.sql = new SQLStatement(sql);
  }

  @Override
  protected PreparedStatement prepareStatement() {
    try {
      PreparedStatement stmt;
      
      if (sql.getQueryType() == SQLStatement.TYPE_DELETE) {
    	    StringBuilder nativeSql = new StringBuilder();
            nativeSql.append(sql.toNativeSQL());
            stmt = conn.prepareStatement(nativeSql.toString());
            return stmt;
      } else if (sql.getQueryType() == SQLStatement.TYPE_SELECT) {
    	  // trade in the named/positional parameters for garden
          // variety jdbc params ( "?" )
          StringBuilder nativeSql = new StringBuilder();
          nativeSql.append(sql.toNativeSQL());
          if(this.maxResults != -1) {
            nativeSql.append(" LIMIT ").append(this.maxResults);
          }
          if(this.firstResult != 0) {
            nativeSql.append(" OFFSET ").append(this.firstResult);
          }
          stmt = conn.prepareStatement(nativeSql.toString());

          // set query parameters
          fillParameters(stmt);
          return stmt; 
      } else {
        throw new RuntimeException("You tried to call createNativeQuery(sql, entityClass) with something else " +
            "besides a select query. Your sql was: " + sql.toString());
      }
    
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  protected void fillParameters(PreparedStatement stmt) throws SQLException {
    for(SQLStatement.Token token : sql.getTokens()) {
      if(token.isParameter()) {
        if(token.getParameterName() != null) {
          stmt.setObject(token.getParameterIndex(), namedParameters.get(token.getParameterName()));
        } else {
          stmt.setObject(token.getParameterPosition(), posParameters.get(token.getParameterPosition()));
        }
      }
    }
  }

  @Override
  public Query setParameter(String name, Object value) {
    assert sql.hasParameter(name) : "No such parameter '" + name + "'";
    namedParameters.put(name, value);
    return this;
  }

  @Override
  public Query setParameter(String name, Calendar value, TemporalType temporalType) {
    throw new UnsupportedOperationException("Calendar stuff not yet implemented");
  }

  @Override
  public Query setParameter(String name, Date value, TemporalType temporalType) {
    namedParameters.put(name, value);
    return this;
  }

  @Override
  public Query setParameter(int position, Object value) {
    posParameters.put(position, value);
    return this;
  }

  @Override
  public Query setParameter(int position, Date value, TemporalType temporalType) {
    posParameters.put(position, value);
    return this;
  }

  @Override
  public Query setParameter(int position, Calendar value, TemporalType temporalType) {
    throw new UnsupportedOperationException("Calendar stuff not yet implemented");
  }
}
