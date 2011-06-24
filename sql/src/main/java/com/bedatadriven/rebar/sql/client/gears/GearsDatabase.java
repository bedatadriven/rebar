package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;

public class GearsDatabase implements SqlDatabase {

  private final String name;
  private final Factory gearsFactory;
  
  public GearsDatabase(String name) {
    this.name = name;
    this.gearsFactory = Factory.getInstance();
  }

  @Override
  public void transaction(SqlTransactionCallback callback) {
    Database db = gearsFactory.createDatabase();
    db.open(name);
    try {
      db.execute("begin");
      
      
      
    } catch(Exception e) {
      try {
        db.execute("rollback");
      } catch (DatabaseException ignored) {
      }
      callback.onError(new SqlException(e));
    }
    
    
  }

}
