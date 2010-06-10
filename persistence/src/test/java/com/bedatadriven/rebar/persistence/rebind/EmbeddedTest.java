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
import com.bedatadriven.rebar.persistence.client.domain.Address;
import com.bedatadriven.rebar.persistence.client.domain.Contact;
import com.bedatadriven.rebar.persistence.client.domain.ContactUnit;
import junit.framework.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

}
