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
import com.bedatadriven.rebar.persistence.client.domain.Event;
import com.bedatadriven.rebar.persistence.client.domain.EventUnit;
import junit.framework.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * @author Alex Bertram
 */
public class DateTest extends PersistenceUnitTestCase {

  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return EventUnit.class;
  }

  @Test
  public void testPersist() throws SQLException {

    EntityManager em = emf.createEntityManager();

    Date today = new Date();

    Event entity = new Event(1, 35, today);
    em.persist(entity);

    em.close();

    // Verify that it has in fact been written to the db

    Connection conn = connectionProvider.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select eventDate from Event");
    Assert.assertTrue(rs.next());
    Assert.assertEquals(today.getTime(), rs.getDate(1).getTime());

    conn.close();

  }
}
