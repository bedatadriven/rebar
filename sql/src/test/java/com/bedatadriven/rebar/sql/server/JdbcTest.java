package com.bedatadriven.rebar.sql.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.bedatadriven.rebar.sql.server.jdbc.JdbcDatabaseFactory;

public class JdbcTest  {


  private int callbackCount = 0;
  private boolean successCalled = false;
  
  @Test
  public void basicTest() throws ClassNotFoundException, SQLException {

    String dbName = TestUtil.uniqueDbName();
    
    SqlDatabaseFactory factory = new JdbcDatabaseFactory();
    SqlDatabase db = factory.open(dbName);
    
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists foobar (id INT, name TEXT)");
        tx.executeSql("insert into foobar (id, name) values (1, 'foo') ");
        tx.executeSql("insert into foobar (id, name) values (2, 'bar') ");
        tx.executeSql("insert into foobar (id, name) values (?, ?) ", new Object[] { 0, null } );

        tx.executeSql("select * from foobar where id > ?", new Object[] { 1 }, new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertThat(results.getRows().size(), equalTo(1));
            assertThat(results.getRow(0).getString("name"), equalTo("bar"));

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
            assertThat(results.getRows().size(), equalTo(3));

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
    assertThat(rs.getInt(1), equalTo(3));
    rs.close();
    conn.close();
  }

  @Test
  public void dateTest() throws ClassNotFoundException, SQLException {

    SqlDatabase db = TestUtil.openUniqueDb();
      
    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table dates (x TEXT)");
        tx.executeSql("insert into dates (x) values (?) ", new Object[] { "2011-01-01" });
        tx.executeSql("select x, strftime('%Y', x) as year from dates", new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertThat("rows", results.getRows().size(), equalTo(1));
            assertThat("x", results.getRow(0).getDate("x"), equalTo(new Date(2011-1900,0,1)));
            assertThat("year", results.getRow(0).getInt("year"), equalTo(2011));
            callbackCount ++;
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
  }
  

  private static long TIME1 = 1316179555000l;
  private static long TIME2 = 380035555000l;
  
  @Test
  public void testTimes() {
  	
    SqlDatabase db = TestUtil.openUniqueDb();

    db.transaction(new SqlTransactionCallback() {
      @Override
      public void begin(SqlTransaction tx) {
        tx.executeSql("create table if not exists times (x REAL)");
        tx.executeSql("insert into times (x) values (?) ", new Object[] { TIME1 });
        tx.executeSql("insert into times (x) values (?) ", new Object[] { TIME2 });
        tx.executeSql("select x, strftime('%Y', x/1000, 'unixepoch') as year from times",  new SqlResultCallback() {
          @Override
          public void onSuccess(SqlTransaction tx, SqlResultSet results) {
            assertThat(results.getRow(0).getDate("x").getTime(), equalTo(TIME1));
            assertThat(results.getRow(1).getDate("x").getTime(), equalTo(TIME2));
            
            assertThat(results.getRow(0).getInt("year"), equalTo(2011));
            assertThat(results.getRow(1).getInt("year"), equalTo(1982));  
          }
        });
      }

			@Override
      public void onError(SqlException e) {
        throw new AssertionError(e);
      }
    });
  }
  
}
