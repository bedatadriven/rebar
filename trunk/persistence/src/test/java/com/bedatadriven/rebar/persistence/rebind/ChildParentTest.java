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
import com.bedatadriven.rebar.persistence.client.domain.Child;
import com.bedatadriven.rebar.persistence.client.domain.ChildParentUnit;
import com.bedatadriven.rebar.persistence.client.domain.Parent;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Alex Bertram
 */
public class ChildParentTest extends PersistenceUnitTestCase {


  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return ChildParentUnit.class;
  }

  @Test
  public void testPersistChild() throws SQLException {

    EntityManager em = emf.createEntityManager();
    Parent parent = new Parent(99, "Fred");
    em.persist(parent);

    Child child = new Child(1, "Kiddo", parent);
    em.persist(child);

    em.close();

    Connection conn = connectionProvider.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select parentId from Child");

    Assert.assertTrue(rs.next());
    Assert.assertEquals(99, rs.getInt(1));
    
  }

  @Test
  public void testLazyParent() throws SQLException {

    EntityManager em = emf.createEntityManager();
    Parent parent = new Parent(99, "Fred");
    em.persist(parent);

    Child child = new Child(1, "Kiddo", parent);
    em.persist(child);

    em.close();

    em = emf.createEntityManager();
    Child rekid = em.find(Child.class, 1);
    Parent rep = rekid.getParent();

    Assert.assertEquals(parent.getName(), rep.getName());
    Assert.assertEquals(parent.getId(), rep.getId());
  }

  @Test @Ignore("not yet implemented")
  public void testCollectionsLoading() throws SQLException {

    // Add our test data to the database
    EntityManager em = emf.createEntityManager();
    Parent parent = new Parent(99, "Fred");
    em.persist(parent);

    em.persist(new Child(301, "Lora lee", parent));
    em.persist(new Child(302, "Billybob", parent));
    em.persist(new Child(303, "Zoella", parent));

    em.close();

    // Verify that the children are found in the collection

    em = emf.createEntityManager();
    parent = em.find(Parent.class, 99);

    Assert.assertEquals(3, parent.getChildren().size());
  }
}
