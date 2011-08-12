package com.bedatadriven.rebar.sql.client.gears;

import java.util.Date;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;


class GearsExecutor implements SyncTransactionAdapter.Executor {

  private String databaseName;
  private Database db;

  public GearsExecutor(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public boolean begin() throws Exception {
    Factory factory = Factory.getInstance();
    if(factory == null) {
      throw new SqlException("Gears is not installed");
    }
    db = factory.createDatabase();
    if(db == null) {
      throw new SqlException("Could not create Gears database");
    }
    db.open(databaseName);
    try {
    	// by including 'EXCLUSIVE' we assure that the transaction begins 
    	// immediately rather than waiting for the first non-select statement.
    	// With 'EXCLUSIVE' we don't risk getting a locked exception on subsequent 
    	// commands
    	db.execute("BEGIN EXCLUSIVE TRANSACTION");
    	return true;
    } catch(Exception e) {
    	try {
    		db.close();
    	} catch(Exception ignored) {
    	}
    	if(e.getMessage().contains("locked")) {
    		return false; // database is locked, we return false to schedule a retry
    	} else {
    		throw e; // some other fatal exception
    	}
    }
  }

  @Override
  public SqlResultSet execute(String statement, Object[] params) throws Exception {
    ResultSet rs = db.execute(statement, toStringArray(params));
    try {
      return new SqlResultSet(db.getLastInsertRowId(), db.getRowsAffected(), 
      		new GearsResultSetRowList(rs));
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
      	if(params[i] == null) {
      		s[i] = null;
      	} else if(params[i] instanceof Date) {
      		s[i] = Long.toString(((Date)params[i]).getTime());
      	} else {
      		s[i] = params[i].toString();
      	}
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
