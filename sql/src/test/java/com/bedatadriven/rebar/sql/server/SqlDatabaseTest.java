package com.bedatadriven.rebar.sql.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;

import com.bedatadriven.rebar.async.NullCallback;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.client.fn.AsyncSql;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlDatabaseTest {
	

  @Test
  public void basicTest() throws ClassNotFoundException, SQLException {

    final SqlDatabase db = TestUtil.openUniqueDb();
    db.executeSql("CREATE TABLE table1 (id INT PRIMARY KEY, name TEXT)");
    db.execute(AsyncSql.dropAllTables(), NullCallback.forVoid());

    db.selectSingleInt("select count(*) from sqlite_master", new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				assertThat(result, equalTo(0));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}
		});
  }
  
  
}
