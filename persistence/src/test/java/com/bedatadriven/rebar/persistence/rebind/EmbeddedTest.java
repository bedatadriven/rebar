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

import com.bedatadriven.rebar.sql.async.mock.MockAsyncConnection;
import org.junit.Test;
import com.bedatadriven.rebar.persistence.client.domain.*;
import com.bedatadriven.rebar.persistence.client.PersistenceUnit;
import com.bedatadriven.rebar.persistence.server.BulkOperationBuilder;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import junit.framework.Assert;

/**
 * @author Alex Bertram
 */
public class EmbeddedTest extends PersistenceUnitTestCase{


  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return ContactUnit.class;
  }

  @Test
  public void testEmbeddedPersist() throws Exception {

    EntityManager em = emf.createEntityManager();

    Contact boss = new Contact();
    boss.setId("AB64");
    boss.setName("Ralph");
    boss.setAddress(new Address("13 kirby road", null, "Sterling Hghts", "MI", 16901, 4));

    em.persist(boss);
    em.close();

  // Verify that it has in fact been written to the db

    Connection conn = connectionProvider.getConnection();
    PreparedStatement stmt = conn.prepareStatement("select id, name, state from Contact");
    ResultSet rs = stmt.executeQuery();

    Assert.assertTrue(rs.next());
    Assert.assertEquals("AB64", rs.getString(1));
    Assert.assertEquals("Ralph", rs.getString(2));
    Assert.assertEquals("MI", rs.getString(3));
//
//    Contact reboss = new Contact();
//    reboss = em.find(Contact.class, boss.getId());
//    Assert.assertEquals(boss, reboss);
//
//    em.close();
  }


  @Test
  public void testEmbeddedFind() throws Exception {

    EntityManager em = emf.createEntityManager();

    Contact boss = new Contact();
    boss.setId("AB64");
    boss.setName("Ralph");
    boss.setAddress(new Address("13 kirby road", null, "Sterling Hghts", "MI", 16901, 4));

    em.persist(boss);
    em.close();

    em = emf.createEntityManager();
    Contact reboss = em.find(Contact.class, boss.getId());

    Assert.assertEquals(boss, reboss);
  }

  @Test
  public void testBulk() throws Exception {

    List<Contact> list = new ArrayList<Contact>();
    list.add(new Contact("RA365", "Ralph", new Address("13 kirby", "Apt 4", "Wellsboro", "PA", 16901, 3)));
    list.add(new Contact("JQ412", "Jim", new Address("13 kirby", null, "Savanahh", "GA", 16901, 3)));
    list.add(new Contact("SZQ", "Suzy", null));

    BulkOperationBuilder builder = new BulkOperationBuilder();
    builder.insert(Contact.class, list);

    String json = builder.asJson();

    System.out.println(json);

    EntityManager em = emf.createEntityManager();  // assure schema is created
    MockAsyncConnection conn = new MockAsyncConnection(connectionProvider.getConnection());
    conn.executeUpdates(json);


    List<Contact> relist = em.createNativeQuery("select * from Contact", Contact.class)
                            .getResultList();

    for(int i=0;i!=list.size();++i)
      Assert.assertEquals(list.get(i), relist.get(i));



  }

}
