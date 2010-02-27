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

package com.bedatadriven.gears.persistence.server;

import org.junit.Test;
import org.json.JSONException;
import com.bedatadriven.gears.persistence.client.domain.Simple;
import com.bedatadriven.gears.persistence.client.domain.SimpleUnit;
import com.bedatadriven.gears.persistence.client.PersistenceUnit;
import com.bedatadriven.gears.persistence.rebind.PersistenceUnitTestCase;
import com.bedatadriven.gears.bulk.mock.MockBulkOperationExecutor;
import com.bedatadriven.gears.bulk.client.BulkException;
import com.bedatadriven.gears.bulk.client.BulkExecutor;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import junit.framework.Assert;

import javax.persistence.EntityManager;

/**
 * @author Alex Bertram
 */
public class MockBulkOperationTest extends PersistenceUnitTestCase {

  @Override
  protected Class<? extends PersistenceUnit> getPersistenceUnit() {
    return SimpleUnit.class;
  }

  @Test
  public void testExecute() throws JSONException, SQLException, BulkException {

    // First, simulate what goes on server-side, that is the
    // generation of a JSON update stream
    List<Simple> list = new ArrayList<Simple>();
    list.add(new Simple(1, "Hello World", true));
    list.add(new Simple(3, null, false));
    list.add(new Simple(9, "Here we are", false));

    BulkOperationBuilder builder = new BulkOperationBuilder();
    builder.insert(Simple.class, list);

    String json = builder.asJson();
    System.out.println(json);

    // Now test the mock execution on the client side
    EntityManager em = emf.createEntityManager();      // this assures the creation of the schema
    MockBulkOperationExecutor executor = new MockBulkOperationExecutor(
        connectionProvider.getConnection());
    executor.executeUpdates(json, new AsyncCallback<Integer>() {
      public void onFailure(Throwable throwable) {
        throw new Error(throwable);
      }

      public void onSuccess(Integer integer) {

      }
    });

    // Now verify that the inserts have occurred
    Statement stmt = connectionProvider.getConnection().createStatement();
    ResultSet rs = stmt.executeQuery("select * from Simple");
    Assert.assertTrue("records present", rs.next());
    rs.close();

    // Verify that the null value was set correctly
    stmt = connectionProvider.getConnection().createStatement();
    rs = stmt.executeQuery("select name from Simple where id=3");
    Assert.assertTrue("id=3 is present", rs.next());
    Assert.assertNull(rs.getString(1));
    Assert.assertTrue(rs.wasNull());
    rs.close();

  }


  @Test
  public void testReRead() throws JSONException, SQLException, BulkException {

    // First, simulate what goes on server-side, that is the
    // generation of a JSON update stream
    List<Simple> list = new ArrayList<Simple>();
    list.add(new Simple(1, "Hello World", true));
    list.add(new Simple(3, null, false));
    list.add(new Simple(9, "Here we are", false));

    BulkOperationBuilder builder = new BulkOperationBuilder();
    builder.insert(Simple.class, list);

    String json = builder.asJson();
    System.out.println(json);

    // Now test the mock execution on the client side
    EntityManager em = emf.createEntityManager();      // this assures the creation of the schema
    BulkExecutor etor = new MockBulkOperationExecutor(connectionProvider.getConnection());
    etor.executeUpdates(json, new AsyncCallback<Integer>() {
      public void onFailure(Throwable throwable) {
        throw new Error(throwable);
      }

      public void onSuccess(Integer integer) {
        
      }
    });

    // Now verify that we can reread the insert records with the EntityManager
    List<Simple> relist = em.createNativeQuery("select * from Simple order by id", Simple.class)
        .getResultList();

    for(int i =0; i!=list.size(); ++i) {
      Assert.assertEquals(list.get(i), relist.get(i));
    }


  }

}
