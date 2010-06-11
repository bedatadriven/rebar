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

package com.bedatadriven.rebar.sync.mock;

import com.bedatadriven.rebar.sync.client.BulkUpdaterAsync;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 *
 * An implementation of BulkExecutor that can be used to test
 * server-generated JSON against an Sqlite database connection.
 *
 * @author Alex Bertram
 */
public class MockBulkUpdater implements BulkUpdaterAsync {

  private Connection connection;

  public MockBulkUpdater(Connection connection) {
    this.connection = connection;
  }

  public void executeUpdates(String databaseName, String json, AsyncCallback<Integer> callback) {
    try {
      callback.onSuccess(executeUpdates(json));
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  public int executeUpdates(String json) throws SQLException, JSONException {
    int totalRowsAffected = 0;
    JSONArray list = new JSONArray(json);
    for (int i = 0; i != list.length(); ++i) {
      JSONObject bulkOperation = list.getJSONObject(i);

      totalRowsAffected += executeStatement(bulkOperation);
    }
    return totalRowsAffected;
  }

  private int executeStatement(JSONObject bulkOperation) throws JSONException, SQLException {
    String statement = bulkOperation.getString("statement");
    PreparedStatement stmt = connection.prepareStatement(statement);

    System.out.println("Preparing Statement: " + statement);

    if(bulkOperation.has("executions")) {
      return executeAllExecutions(bulkOperation, statement, stmt);
    } else {
      return stmt.executeUpdate();
    }
  }

  private int executeAllExecutions(JSONObject bulkOperation, String sql, PreparedStatement stmt) throws SQLException, JSONException {
    int rowsAffected = 0;
    ParameterMetaData paramMetaData = stmt.getParameterMetaData();
    JSONArray parameterSets = bulkOperation.getJSONArray("executions");

    for (int j = 0; j != parameterSets.length(); ++j) {

      JSONArray params = parameterSets.getJSONArray(j);

      System.out.println("Executing Statement with parameters: " + params.toString());

      for (int k = 0; k != params.length(); ++k) {
        doSetParameter(stmt, paramMetaData, params, k);
      }
      try {
        rowsAffected += stmt.executeUpdate();
      } catch (SQLException e) {
        throw new Error("Exception thrown while executing the statement: '" + sql +
            "' with parameter set " + j + ": " + params.toString(), e);
      }
    }
    return rowsAffected;
  }

  private void doSetParameter(PreparedStatement stmt, ParameterMetaData paramMetaData, JSONArray params, int j) throws SQLException, JSONException {
    try {
      if(params.isNull(j)) {
        stmt.setNull(j+1, paramMetaData.getParameterType(j));
      } else {
        switch (paramMetaData.getParameterType(j)) {
          case Types.BIGINT:
            stmt.setLong(j + 1, Long.parseLong(params.getString(j)));
            break;
          case Types.BOOLEAN:
            stmt.setBoolean(j + 1, params.getBoolean(j));
            break;
          case Types.CHAR:
          case Types.NVARCHAR:
          case Types.VARCHAR:
          case Types.LONGNVARCHAR:
          case Types.LONGVARCHAR:
            stmt.setString(j + 1, params.getString(j));
            break;
          case Types.DATE:
          case Types.TIME:
          case Types.TIMESTAMP:
            stmt.setDate(j + 1, new Date(params.getLong(j)));
            break;
          case Types.DECIMAL:
          case Types.DOUBLE:
          case Types.FLOAT:
            stmt.setDouble(j + 1, params.getDouble(j));
            break;
          case Types.INTEGER:
          case Types.TINYINT:
          case Types.SMALLINT:
            stmt.setInt(j + 1, params.getInt(j));
            break;
          default:
            throw new AssertionError("Unknown/unhandled SQL type");
        }
      }
    } catch (Exception e) {

      String paramValue = "[error]";
      try {
        paramValue = params.getString(j);
      } catch (Throwable ignored) {
      }
      throw new Error("Exception thrown while setting parameter index " + j + ", " +
          "parameter type of " + paramMetaData.getParameterType(j) + " (See java.sql.Types)." +
          "The JSON value was '" + paramValue + "'", e);
    }
  }
}
