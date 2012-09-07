package com.bedatadriven.rebar.sql.client.fn;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AsyncSql {

	public static <F> TxAsyncFunction<F, Void> sequence(TxAsyncFunction<F, ?>... f) {
		return new TxAsyncSequence<F, Void>(f);
	}
	
	public static TxAsyncFunction<Void, Void> dropAllTables() {
		return QueryFunction.query("select name from sqlite_master where type = 'table'")
				.parallelMap(new DropUserTableFunction())
				.discardResult();
				
	}
	
	public static <F,T> TxAsyncFunction<F, T> valueOf(final Function<F,T> f) {
		return new TxAsyncFunction<F, T>() {

			@Override
      protected void doApply(SqlTransaction tx, F argument,
          AsyncCallback<T> callback) {
				callback.onSuccess(f.apply(argument));
      }
		};
	}
	
	private static class DropUserTableFunction extends TxAsyncFunction<SqlResultSetRow, Void> {

		@Override
		protected void doApply(SqlTransaction tx, 
				SqlResultSetRow row,
				final AsyncCallback<Void> callback) {
			String tableName = row.getString("name");
			// some implementations may store metadata in tables that cannot be deleted,
			// __WebKitMetadata__ for example
			if(tableName.startsWith("_") || tableName.startsWith("sqlite_")) {
				callback.onSuccess(null);
			} else {
				tx.executeSql("DROP TABLE " + tableName, new SqlResultCallback() {

					@Override
					public void onSuccess(SqlTransaction tx,
							SqlResultSet results) {
						callback.onSuccess(null);
					}
					@Override
					public boolean onFailure(SqlException e) {
						// ignore other errors; there may be protected tables introduced
						// by other implementations
						callback.onSuccess(null);
						return SqlResultCallback.CONTINUE;
					}
				});
				
			}
		}
		
	}

	public static TxAsyncFunction<Void, Void> ddl(final String sql) {
		return new TxAsyncFunction<Void, Void>() {
			
			@Override
			protected void doApply(SqlTransaction tx, Void argument,
					final AsyncCallback<Void> callback) {
				tx.executeSql(sql, new SqlResultCallback() {
					
					@Override
					public void onSuccess(SqlTransaction tx, SqlResultSet results) {
						callback.onSuccess(null);
					}
				});
			}
		};
	}
}
