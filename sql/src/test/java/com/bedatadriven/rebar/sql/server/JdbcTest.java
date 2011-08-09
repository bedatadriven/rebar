package com.bedatadriven.rebar.sql.server;

import com.bedatadriven.rebar.sql.client.*;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JdbcTest  {


  private int callbackCount = 0;
  private boolean successCalled = false;
  
  @Test
  public void basicTest() throws ClassNotFoundException, SQLException {

    String dbName = "target/testdb" + new Date().getTime();

    SqlDatabaseFactory factory = new JdbcDatabaseFactory();
    SqlDatabase db = factory.open(dbName);
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists foobar (id INT, name TEXT)");
        tx.executeSql("insert into foobar (id, name) values (1, 'foo') ");
        tx.executeSql("insert into foobar (id, name) values (2, 'bar') ");
        tx.executeSql("select * from foobar where id > ?", new Object[] { 1 }, new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertThat(results.getRows().length(), equalTo(1));
            assertThat(results.getRows().getRow(0).getString("name"), equalTo("bar"));

            callbackCount ++;
          }

          @Override
          public boolean onFailure(SqlException e) {
            throw new AssertionError(e);
          }
        });

        // try without params
        tx.executeSql("select * from foobar", new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertThat(results.getRows().length(), equalTo(2));

            callbackCount++;
          }

          @Override
          public boolean onFailure(SqlException e) {
            throw new AssertionError(e);
          }
        });
      }

      @Override
      public void onSuccess() {
      	successCalled = true;
      }

			@Override
      public void onError(SqlException e) {
        throw new AssertionError(e);
      }
    });

    assertThat("rs callback count", callbackCount, equalTo(2));
    assertThat("successCalled", successCalled, equalTo(true));
    
    // verify that commit was called

    Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);

    PreparedStatement stmt = conn.prepareStatement("select count(*) from foobar");
    ResultSet rs = stmt.executeQuery();
    assertTrue(rs.next());
    assertThat(rs.getInt(1), equalTo(2));
    rs.close();
    conn.close();

  }

}
