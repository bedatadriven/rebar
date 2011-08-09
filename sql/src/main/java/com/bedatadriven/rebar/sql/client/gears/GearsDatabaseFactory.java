package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlDatabaseFactory;
import com.bedatadriven.rebar.sql.client.SqlDatabase;

public class GearsDatabaseFactory implements SqlDatabaseFactory {

  @Override
  public SqlDatabase open(String databaseName) {
    return new GearsDatabase(databaseName);
  }
  

}
