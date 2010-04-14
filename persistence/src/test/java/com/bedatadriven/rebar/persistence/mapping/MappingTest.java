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

package com.bedatadriven.rebar.persistence.mapping;

import com.bedatadriven.rebar.persistence.client.domain.*;
import junit.framework.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @author Alex Bertram
 */
public class MappingTest {

  @Test
  public void testAssumptions() {

    Assert.assertEquals("java.lang.String", new TypeInfo.RuntimeImpl(String.class).getQualifiedName());
    Assert.assertEquals("String", new TypeInfo.RuntimeImpl(String.class).getSimpleName());
    Assert.assertEquals("int", new TypeInfo.RuntimeImpl(Integer.TYPE).getSimpleName());
  }

  @Test
  public void testSimpleMapping() throws SQLException {

    UnitMapping context = new UnitMapping(SimpleUnit.class);
    EntityMapping entity = new EntityMapping(context, Simple.class);

    Assert.assertEquals(3, entity.getProperties().size());

//        testCreateStatement(entity);
  }

  @Test
  public void testEmbeddedMapping() {
    UnitMapping context = new UnitMapping(ContactUnit.class);
    EntityMapping entity = new EntityMapping(context, Contact.class);

    dumpMapping(entity);

    Assert.assertEquals(3, entity.getProperties().size());
    Assert.assertEquals(8, entity.getColumns().size());
  }

  @Test
  public void testBoxedPrimitives() {
    UnitMapping context = new UnitMapping(AdminUnit.class);
    EntityMapping entity = new EntityMapping(context, Bounds.class);

    dumpMapping(entity);

    Assert.assertEquals(4, entity.getProperties().size());
  }

  @Test
  public void testGettersWithArgsIgnored() {
    UnitMapping unit = new UnitMapping(AdminUnit.class);
    EntityMapping entity = new EntityMapping(unit, Province.class);
                     
    dumpMapping(entity);
    
    Assert.assertEquals(4, entity.getProperties().size());
  }

  @Test
  public void testGeneratedValueIsIgnored() {
    UnitMapping unit = new UnitMapping(ChildParentUnit.class);
    EntityMapping entity = new EntityMapping(unit, Parent.class);
    Assert.assertTrue("insertable", entity.getId().isInsertable());
    Assert.assertFalse("not updatable", entity.getId().isUpdatable());
    Assert.assertFalse("not autoincrement", entity.getId().isAutoincrement());
    Assert.assertTrue("unique", entity.getId().isUnique());
  }

  @Test
  public void testClientSideGeneratedValue() {
    UnitMapping unit = new UnitMapping(AutoIdUnit.class);
    EntityMapping entity = new EntityMapping(unit, AutoIdEntity.class);
    Assert.assertFalse("not insertable", entity.getId().isInsertable());
    Assert.assertFalse("not updatable", entity.getId().isUpdatable());
    Assert.assertTrue("autoincrement", entity.getId().isAutoincrement());
    Assert.assertTrue("unique", entity.getId().isUnique());
  }

  private void dumpMapping(EntityMapping entity) {
    for (PropertyMapping mapping : entity.getProperties()) {
      System.out.println(mapping.getName() + ": " + mapping.getClass().getName());
      for (ColumnMapping column : mapping.getColumns()) {
        System.out.println("    " + column.getName() + " " + column.getType());
      }
    }
  }
}
