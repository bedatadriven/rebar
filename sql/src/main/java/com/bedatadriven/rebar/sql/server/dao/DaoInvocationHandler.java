package com.bedatadriven.rebar.sql.server.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.persistence.NoResultException;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.rebind.QueryTargetClass;
import com.bedatadriven.rebar.sql.rebind.SelectMethod;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DaoInvocationHandler implements InvocationHandler {
	private SqlDatabase database;
	
	public DaoInvocationHandler(SqlDatabase database) {
	  this.database = database;
  }

	@Override
  public Object invoke(Object proxy, Method method, final Object[] args)
      throws Throwable {
	 
		final SelectMethod query = new SelectMethod(method);
		final Collector collector = new Collector();
		executeSelectAsync(query, args, collector);
		
		return collector.result;
	}
	
		
	private void executeSelectAsync( final SelectMethod query,
			final Object[] args,
			final AsyncCallback callback) {
	  database.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				tx.executeSql(query.getSql(), query.buildParamArray(args), new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						callback.onSuccess(map(query, results));
					}
				});
			}

			@Override
      public void onError(SqlException e) {
	      callback.onFailure(e);
      }
		
		});
  };
  
	private Object map(SelectMethod select, SqlResultSet results) {
		if(select.isSingleResult()) {
			if(results.getRows().size() < 1) {
				throw new NoResultException();
			} 
			return map(select, results.getRow(0));
		} else {
			throw new RuntimeException("nyi");
		}
  }
	
	
	private Object map(SelectMethod select, SqlResultSetRow row) {
		QueryTargetClass clazz = select.getResultClass();
		return clazz.toInstance(row);
	}


	private static class Collector implements AsyncCallback {

		private Object result = null;
		
		@Override
    public void onFailure(Throwable caught) {
			if(caught instanceof RuntimeException) {
				throw (RuntimeException)caught;
			} else if(caught instanceof Error) {
				throw (Error)caught;
			} else {
				throw new RuntimeException(caught.getMessage(), caught);
			}
    }

		@Override
    public void onSuccess(Object result) {
			this.result = result;
    }
	}	
}
