/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.sync.server;

import com.bedatadriven.rebar.sync.mock.MockBulkUpdater;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuilderTest {
  protected Connection conn;
  protected JpaUpdateBuilder builder;
  protected MockBulkUpdater updater;


  @Before
  public void setUp() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection("jdbc:sqlite:buildertest.db");
    builder = new JpaUpdateBuilder();
    updater = new MockBulkUpdater(conn);

    Statement clean = conn.createStatement();
    clean.executeUpdate("drop table if exists Contact");
    clean.executeUpdate("drop table if exists Person");
  }

  @After
  public void cleanUp() throws SQLException {
    conn.close();
  }

  @Test
  public void simpleEntity() throws JSONException, SQLException {
    List<Person> list = new ArrayList<Person>();
    list.add(new Person(1, "Bob", makeDate(1982, 1, 16), 1.7, true));
    list.add(new Person(2, "Jim", makeDate(1980, 4, 10), 1.326, true));
    list.add(new Person(3, "Sally", makeDate(1981, 11, 10), 1.7, false));
    list.add(new Person(4, "Jean", makeDate(1981, 11, 11), 0.9, true));

    builder.createTableIfNotExists(Person.class);
    builder.insert(Person.class, list);

    executeUpdateAndExpectedAffectedRowsToBe(4);

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id, name, birthDate, height, active from Person order by id");

    for(int i=0;i!=list.size();++i) {
      rs.next();
      assertEquals(list.get(i).getId(), rs.getInt(1));
      assertEquals(list.get(i).getName(), rs.getString(2));
      assertEquals(list.get(i).getBirthDate(), rs.getDate(3));
      assertEquals(list.get(i).getHeight(), rs.getDouble(4));
      assertEquals(list.get(i).isActive(), rs.getBoolean(5));
    }
    rs.close();
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

    updater.executeUpdates(json, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        throw new Error(throwable);
      }

      @Override
      public void onSuccess(Integer rows) {
        assertEquals("rows updated", 3, (int)rows);
      }
    });

    // now verify that the data were populated correctly
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select id, city from Contact order by id");

    for(int i=0;i!=3;++i) {
      assertTrue(rs.next());
      assertEquals(list.get(i).getId(), rs.getString(1));
      if(list.get(i).getAddress() != null)
        assertEquals(list.get(i).getAddress().getCity(), rs.getString(2));
    }
  }

  private void executeUpdateAndExpectedAffectedRowsToBe(final int expectedRowsAffected) throws JSONException {
    updater.executeUpdates(builder.asJson(), new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        throw new Error(throwable);
      }

      @Override
      public void onSuccess(Integer rowsAffected) {
        assertEquals("rowsAffected", expectedRowsAffected, (int)rowsAffected);
      }
    });
  }

  private java.util.Date makeDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month-1);
    cal.set(Calendar.DATE, day);
    return cal.getTime();
  }

}
