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

import com.bedatadriven.rebar.sync.mock.MockBulkUpdater;
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

    int rows = updater.executeUpdates(json);
    assertEquals("rows updated", 3, rows);

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

  private void executeUpdateAndExpectedAffectedRowsToBe(final int expectedRowsAffected) throws JSONException, SQLException {
    int rows = updater.executeUpdates(builder.asJson());
    assertEquals("rowsAffected", expectedRowsAffected, rows);
  }

  private java.util.Date makeDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month-1);
    cal.set(Calendar.DATE, day);
    return cal.getTime();
  }

}
