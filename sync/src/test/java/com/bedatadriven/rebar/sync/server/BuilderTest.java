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

package com.bedatadriven.rebar.sync.server;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.server.jdbc.SqliteStubDatabase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuilderTest {
  protected JpaUpdateBuilder builder;
  protected SqlDatabase database;
  private String connectionUrl;


  @Before
  public void setUp() throws ClassNotFoundException, SQLException {

    String databaseName = "buildertest" + new java.util.Date().getTime();
    System.out.println("db = " + databaseName);

    Class.forName("org.sqlite.JDBC");
    connectionUrl = "jdbc:sqlite:" + databaseName;


    builder = new JpaUpdateBuilder();
    database = new SqliteStubDatabase(databaseName);
  }


  @Test
  public void simpleEntity() throws Exception {
    List<Person> list = new ArrayList<Person>();
    list.add(new Person(1, "Bob", makeDate(1982, 1, 16), 1.7, true));
    list.add(new Person(2, "Jim", makeDate(1980, 4, 10), 1.326, true));
    list.add(new Person(3, "Sally", makeDate(1981, 11, 10), 1.7, false));
    list.add(new Person(4, "Jean", makeDate(1981, 11, 11), 0.9, true));

    builder.createTableIfNotExists(Person.class);
    builder.insert(Person.class, list);

    executeUpdateAndExpectedAffectedRowsToBe(4);

    Connection conn = DriverManager.getConnection(connectionUrl);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id, name, birthDate, height, active, lastVisit from Person order by id");

    for (int i = 0; i != list.size(); ++i) {
      rs.next();
      assertEquals(list.get(i).getId(), rs.getInt(1));
      assertEquals(list.get(i).getName(), rs.getString(2));
      if (i == 0) {
        assertEquals("1982-01-16", rs.getString(3));
      }
      assertEquals(list.get(i).getHeight(), rs.getDouble(4));
      assertEquals(list.get(i).isActive(), rs.getBoolean(5));
      assertTrue(rs.getDouble(6) > 1316171856l);
    }
    rs.close();
    conn.close();
  }

  @Test
  public void embeddedEntities() throws Exception {

    List<Contact> list = new ArrayList<Contact>();
    list.add(new Contact("JQ412", "Jim", new Address("13 kirby", null, "Savanahh", "GA", 16901, 3)));
    list.add(new Contact("RA365", "Ralph", new Address("13 kirby", "Apt 4", "Wellsboro", "PA", 16901, 3)));
    list.add(new Contact("SZQ", "Suzy", null));

    builder = new JpaUpdateBuilder();
    builder.createTableIfNotExists(Contact.class);
    builder.insert(Contact.class, list);

    String json = builder.asJson();

    System.out.println(json);

    database.executeUpdates(json, new AsyncCallback<Integer>() {

      @Override
      public void onSuccess(Integer rows) {
        assertEquals("rows updated", 3, (int) rows);
      }

      @Override
      public void onFailure(Throwable caught) {
        throw new AssertionError(caught);
      }
    });


    // now verify that the data were populated correctly
    Connection conn = DriverManager.getConnection(connectionUrl);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id, city from Contact order by id");

    for (int i = 0; i != 3; ++i) {
      assertTrue(rs.next());
      assertEquals(list.get(i).getId(), rs.getString(1));
      if (list.get(i).getAddress() != null)
        assertEquals(list.get(i).getAddress().getCity(), rs.getString(2));
    }
    rs.close();
    conn.close();
  }

  private void executeUpdateAndExpectedAffectedRowsToBe(final int expectedRowsAffected) throws Exception {
    database.executeUpdates(builder.asJson(), new AsyncCallback<Integer>() {

      @Override
      public void onSuccess(Integer rows) {
        assertEquals("rowsAffected", expectedRowsAffected, (int) rows);
      }

      @Override
      public void onFailure(Throwable caught) {
        throw new AssertionError(caught);
      }
    });
  }

  private java.util.Date makeDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DATE, day);
    return cal.getTime();
  }

}
