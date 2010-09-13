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
import com.bedatadriven.rebar.persistence.client.domain.AutoIdEntity;
import com.bedatadriven.rebar.persistence.client.domain.AutoIdUnit;
import junit.framework.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * @author Alex Bertram
 */
public class AutoIdTest extends PersistenceUnitTestCase {

  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return AutoIdUnit.class;
  }

  @Test
  public void testGenerate() {

    AutoIdEntity bob = new AutoIdEntity("Bob");

    EntityManager em = emf.createEntityManager();
    em.persist(bob);
    em.close();

    Assert.assertTrue("id has been generated", bob.getId() != 0);

    em = emf.createEntityManager();
    AutoIdEntity rebob = em.find(AutoIdEntity.class, bob.getId());

    Assert.assertEquals(bob.getName(), rebob.getName());
   
  }

}


