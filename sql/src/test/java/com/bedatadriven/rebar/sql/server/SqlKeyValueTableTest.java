package com.bedatadriven.rebar.sql.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;
import com.bedatadriven.rebar.sql.client.util.SqlKeyValueTable;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlKeyValueTableTest {
	

	@Test
	public void simpleTest() {
		
    String dbName = "target/keyvalueTest" + new Date().getTime();

    SqlDatabaseFactory factory = new JdbcDatabaseFactory();
    SqlDatabase db = factory.open(dbName);
    SqlKeyValueTable<String, String> table = new SqlKeyValueTable<String, String>(db, "sync_regions", "id", "lastUpdate");
        
    table.put("foo", "bar");
    table.get("foo", new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				assertThat(result, equalTo("bar"));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}
		});
	}
	
	
}
