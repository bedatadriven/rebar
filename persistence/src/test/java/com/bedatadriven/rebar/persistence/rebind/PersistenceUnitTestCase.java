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

import com.bedatadriven.rebar.persistence.client.ConnectionProvider;
import com.bedatadriven.rebar.persistence.client.PersistenceUnit;
import com.bedatadriven.rebar.persistence.mock.MockConnectionProvider;
import com.bedatadriven.rebar.persistence.mock.MockPersistenceUnitFactory;

import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.After;

/**
 * @author Alex Bertram
 */
public abstract class PersistenceUnitTestCase {

  protected ConnectionProvider connectionProvider;
  protected EntityManagerFactory emf;

  @Before
  public void beforeTest() throws Exception {

    MockPersistenceUnitFactory compiler = new MockPersistenceUnitFactory();
    PersistenceUnit unit = compiler.create(getPersistenceUnit());

    connectionProvider = new MockConnectionProvider();
    emf = unit.createEntityManagerFactory(connectionProvider);
  }

  protected abstract Class<? extends PersistenceUnit> getPersistenceUnit();

  @After
  public void afterTest() {
    if(connectionProvider!=null)
      connectionProvider.close();
  }
}
