package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

import java.util.Arrays;


class GearsExecutor implements SyncTransactionAdapter.Executor {

  private String databaseName;
  private Database db;

  public GearsExecutor(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public void begin() throws Exception {
    Factory factory = Factory.getInstance();
    if(factory == null) {
      throw new SqlException("Gears is not installed");
    }
    db = factory.createDatabase();
    if(db == null) {
      throw new SqlException("Could not create Gears database");
    }
    db.open(databaseName);
    db.execute("BEGIN TRANSACTION");
  }

  @Override
  public SqlResultSet execute(String statement, Object[] params) throws Exception {
    ResultSet rs = db.execute(statement, toStringArray(params));
    try {
      return new GearsResultSet(db, rs);
    } finally {
      try { rs.close(); } catch(Exception ignored) {}
    }
  }

  private String[] toStringArray(Object[] params) {
    if(params == null) {
      return new String[0];
    } else {
      String[] s = new String[params.length];
      for(int i=0;i!=params.length;++i) {
        s[i] = params[i].toString();
      }
      return s;
    }
  }

  @Override
  public void commit() throws DatabaseException {
  	try {
  		db.execute("END TRANSACTION");
  	} finally {
  		db.close();
  	}
  }

	@Override
  public void rollback() throws Exception {
  	try {
  		db.execute("ROLLBACK TRANSACTION");
  	} finally {
  		db.close();
  	}	  
  }
}
