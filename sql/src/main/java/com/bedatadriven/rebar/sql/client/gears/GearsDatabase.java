package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.*;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;

class GearsDatabase extends SqlDatabase {

  private final String name;

  public GearsDatabase(String name) {
    this.name = name;
  }

  @Override
  public void transaction(SqlTransactionCallback callback) {
    new SyncTransactionAdapter(new GearsExecutor(name), Scheduler.get(), callback);
  }

}
