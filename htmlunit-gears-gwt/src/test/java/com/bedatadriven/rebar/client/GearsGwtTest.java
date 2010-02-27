/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.client;

import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.junit.client.GWTTestCase;

public class GearsGwtTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.GearsGwtTest";
  }

  public void testCreateFactory() throws Exception {
    Factory factory = Factory.getInstance();
    assertNotNull("Gears factory is not available", factory);
  }

  public void testAccessDatabase() throws Exception {
    Factory factory = Factory.getInstance();
    assertNotNull("Gears factory is not available", factory);

    Database db = factory.createDatabase();
    assertNotNull("Database is null", db);
    db.open();

  }
}
