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

package com.bedatadriven.rebar.persistence.rebind;

import com.bedatadriven.rebar.persistence.client.PersistenceUnit;
import com.bedatadriven.rebar.persistence.client.domain.Person;
import com.bedatadriven.rebar.persistence.client.domain.PersonUnit;
import com.bedatadriven.rebar.persistence.server.BulkOperationBuilder;
import com.bedatadriven.rebar.sql.async.mock.MockAsyncConnection;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.junit.Test;
import org.json.JSONException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.SQLException;

import junit.framework.Assert;

/**
 * @author Alex Bertram
 */
public class PersonTest extends PersistenceUnitTestCase {

  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return PersonUnit.class;
  }

  private Date makeDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month-1);
    cal.set(Calendar.DATE, day);
    return cal.getTime();
  }

  @Test
  public void testBulk() throws JSONException, SQLException {
    List<Person> list = new ArrayList<Person>();
    list.add(new Person(1, "Bob", makeDate(1982, 1, 16), 1.7, true));
    list.add(new Person(2, "Jim", makeDate(1980, 4, 10), 1.326, true));
    list.add(new Person(3, "Sally", makeDate(1981, 11, 10), 1.7, false));
    list.add(new Person(4, "Jean", makeDate(1981, 11, 11), 0.9, true));

    BulkOperationBuilder builder = new BulkOperationBuilder();
    builder.insert(Person.class, list);
    String json = builder.asJson();

    EntityManager em = emf.createEntityManager(); // assure that the schme a is created
    MockAsyncConnection conn = new MockAsyncConnection(connectionProvider.getConnection());
    conn.executeUpdates(json, new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {

      }

      @Override
      public void onSuccess(Integer integer) {

      }
    });

    List<Person> relist = em.createNativeQuery("select * from Person order by id", Person.class)
        .getResultList();
    
    for(int i=0;i!=list.size();++i)
      Assert.assertEquals(list.get(i), relist.get(i));


  }

  

}
