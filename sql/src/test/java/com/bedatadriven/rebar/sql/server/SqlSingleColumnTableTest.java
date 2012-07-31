package com.bedatadriven.rebar.sql.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.bedatadriven.rebar.async.NullCallback;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.util.SqlSingleColumnTable;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlSingleColumnTableTest {

	@Test
	public void test() {
		SqlDatabase db = TestUtil.openUniqueDb();
		final SqlSingleColumnTable<String> table = new SqlSingleColumnTable<String>(db, "sync_history", "lastUpdated");
		
		db.execute(table.createTableIfNotExists(), NullCallback.forVoid());
		
		
		table.put("foobar", new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				table.get(new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						assertThat(result, equalTo("foobar"));
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						throw new AssertionError(caught);
						
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
				
			}
		});
		
	}
	
}
