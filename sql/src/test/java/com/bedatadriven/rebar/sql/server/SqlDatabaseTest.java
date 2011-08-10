package com.bedatadriven.rebar.sql.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlDatabaseTest {
	

  @Test
  public void basicTest() throws ClassNotFoundException, SQLException {

    String dbName = "target/testdb" + new Date().getTime();

    SqlDatabaseFactory factory = new JdbcDatabaseFactory();
    SqlDatabase db = factory.open(dbName);
    db.executeSql("CREATE TABLE table1 (id INT PRIMARY KEY, name TEXT)");
    db.dropAllTables();
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
