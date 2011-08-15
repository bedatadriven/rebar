package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.query.SqlDialect;
import com.bedatadriven.rebar.sql.client.query.SqliteDialect;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gwt.user.client.rpc.AsyncCallback;

//TODO: make abstract. testers should use the SqliteStubDatabase subclass 
public class JdbcDatabase extends SqlDatabase {

  private final String databaseName;

  public JdbcDatabase(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public void transaction(SqlTransactionCallback callback) {
		new SyncTransactionAdapter(newExecutor(), JdbcScheduler.get(), callback);
		
		processEventQueue();
  }

  
	@Override
  public SqlDialect getDialect() {
		return SqliteDialect.INSTANCE;
  }

	protected JdbcExecutor newExecutor() {
	  return new SqliteExecutor("jdbc:sqlite:" + databaseName);
  }

	@Override
  public String getName() {
	  return databaseName;
  }
  
	/**
	 * Processes all pending tasks in the database execution queue.
	 * (Normally shouldn't need to be called)
	 */
	public void processEventQueue() {
		JdbcScheduler.get().process();
	}
  
	public String selectString(String sqlStatement, Object... params) { 
		SqlResultSet rs = assertSingleRow(sqlStatement, params);
		return rs.getRow(0).getSingleString();
	}
	
	public int selectInt(String sqlStatement, Object... params) { 
		SqlResultSet rs = assertSingleRow(sqlStatement, params);
		return rs.getRow(0).getSingleInt();
	}

	private SqlResultSet assertSingleRow(String sqlStatement, Object... params)
      throws AssertionError {
	  SqlResultSet rs = execute(sqlStatement, params);
		if(rs.getRows().size() != 1) {
			throw new AssertionError("For query [" + sqlStatement + "], expected exactly 1 row, got " + rs.getRows().size() + " row(s).");
		}
	  return rs;
  }
	

	public SqlResultSet execute(String sqlStatement, Object... params)
      throws AssertionError {
	  JdbcExecutor executor = newExecutor();
		try {
	    executor.begin();
    } catch (Exception beginException) {
	    throw new RuntimeException(beginException);
    }
		SqlResultSet rs;
		try {
			rs = executor.execute(sqlStatement, params);
		} catch(Exception e) {
			try { executor.rollback(); } catch(Exception ignored) {}
			throw new AssertionError(e);
		} 
		try {
			executor.commit();
		} catch(Exception e) {
			throw new RuntimeException("Exception thrown while committing read transaction", e);
		}
		return rs;

  }

	public void executeUpdates(final String json, final AsyncCallback<Integer> callback) {
		transaction(new SqlTransactionCallback() {

			@Override
			public void begin(SqlTransaction tx) {
				JsonParser parser = new JsonParser();
				JsonArray list = parser.parse(json).getAsJsonArray();
				for (int i = 0; i != list.size(); ++i) {
					JsonObject statement = list.get(i).getAsJsonObject();
					if(statement.has("executions")) {
						enqueueExecutions(statement, tx);
					} else {
						tx.executeSql(statement.get("statement").getAsString());  	    			
					}
				}
			}

			@Override
      public void onSuccess() {
	      callback.onSuccess(0);
      }

			@Override
      public void onError(SqlException e) {
				callback.onFailure(e);
      }
		});
	}
  
  private void enqueueExecutions(JsonObject statement, SqlTransaction tx)  {
  	String sql = statement.get("statement").getAsString();
  	JsonArray parameterSets = statement.get("executions").getAsJsonArray();
  	
    for (int j = 0; j != parameterSets.size(); ++j) {
    	tx.executeSql(sql, toParamArray(parameterSets.get(j).getAsJsonArray()));
    }
  }

  private Object[] toParamArray(JsonArray parameters) {
  	Object[] values = new Object[parameters.size()];
  	for(int i=0;i!=parameters.size();++i) {
  		JsonElement value = parameters.get(i);
  		if(value.isJsonNull()) {
  			values[i] = null;
  		} else if(value.isJsonPrimitive()) {
  			JsonPrimitive primitiveValue = value.getAsJsonPrimitive();
  			if(primitiveValue.isBoolean()) {
  				values[i] = primitiveValue.getAsBoolean();
  			} else if(primitiveValue.isNumber()) {
  				values[i] = primitiveValue.getAsNumber();
  			} else {
  				values[i] = primitiveValue.getAsString();
  			}
  		} else {
  			throw new RuntimeException("expected null or primitive, got " + value.getClass().getSimpleName());
  		}
  	}
  	return values;
  }
	
}
