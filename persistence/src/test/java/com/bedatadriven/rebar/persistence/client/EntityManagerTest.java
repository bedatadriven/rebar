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

package com.bedatadriven.rebar.persistence.client;

import com.bedatadriven.rebar.persistence.client.PersistenceUnit;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.ResultSet;
import com.bedatadriven.rebar.persistence.client.domain.*;
import com.google.gwt.junit.client.GWTTestCase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;

/**
 * @author Alex Bertram
 */
public class EntityManagerTest extends GWTTestCase {

  private String dbName;
  private ConnectionProvider connectionProvider;

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.persistence.PersistenceTest";
  }


  private String getSingleUseDb() {
    return "Single" + new Date().getTime();
  }


  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    dbName = getSingleUseDb();
    connectionProvider = new GearsConnectionProvider(dbName);
  }

  public void testPersist() throws Exception {

    // Now try to persist an entity
    PersistenceUnit unit =  GWT.create(SimpleUnit.class);
    EntityManagerFactory emf = unit.createEntityManagerFactory(connectionProvider);
    EntityManager em = emf.createEntityManager();

    Simple bob = new Simple(1, "Bob", false);

    em.persist(bob);

    // Verify that the entity is a member of the context
    assertTrue(em.contains(bob));

    // Verify that the entity can be reread and taht object identity is preserved
    Simple rebob = em.find(Simple.class, 1);
    assertEquals(bob.getId(), rebob.getId());
    assertEquals(bob.getName(), rebob.getName());
    assertTrue("Object identity is preserved", bob == rebob);

    em.close();

    // Try to open a new session
    em = emf.createEntityManager();
    rebob = em.find(Simple.class, 1);
    assertEquals(bob.getId(), rebob.getId());
    assertEquals(bob.getName(), rebob.getName());
    assertTrue("Object identity NOT  preserved between sessions", bob != rebob);
    em.close();


    // Verify that the entity has actually been persisted

    Database db = Factory.getInstance().createDatabase();
    db.open(dbName);
    ResultSet rs = db.execute("select id, name from Simple");
    assertTrue(rs.isValidRow());
    assertEquals(bob.getId(), rs.getFieldAsInt(0));
    assertEquals(bob.getName(), rs.getFieldAsString(1));
  }


  public void testLazyLoad() {
    // Now try to persist an entity
    String dbName = getSingleUseDb();

    PersistenceUnit unit = GWT.create(SimpleUnit.class);
    EntityManagerFactory emf = unit.createEntityManagerFactory(connectionProvider);
    EntityManager em = emf.createEntityManager();

    Simple bob = new Simple(1, "Bob", false);
    em.persist(bob);
    em.close();

    em = emf.createEntityManager();
    Simple rebob = em.getReference(Simple.class, 1);

    assertEquals(1, rebob.getId());
    assertEquals(bob.getName(), rebob.getName());
    assertTrue(rebob == em.getReference(Simple.class, 1));
  }

  public void testChildParent() {
    PersistenceUnit unit =  GWT.create(ChildParentUnit.class);
    EntityManagerFactory emf = unit.createEntityManagerFactory(connectionProvider);

    // First, store a parent with two children
    EntityManager em = emf.createEntityManager();

    Parent dad = new Parent(1, "Frank");
    em.persist(dad);

    Child billyRae = new Child(10, "Billy Rae", dad);
    em.persist(billyRae);

    Child lauraLee = new Child(20, "Laura Lee", dad);
    em.persist(lauraLee);

    em.close();

    // Now try to read the child and its parent

    em = emf.createEntityManager();
    Child rebilly = em.find(Child.class, 10);

    assertEquals(billyRae.getName(), rebilly.getName());
    assertEquals(dad.getId(), rebilly.getParent().getId());
    assertEquals(dad.getName(), rebilly.getParent().getName());
    em.close();

  }
}
