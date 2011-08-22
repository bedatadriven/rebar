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
  
  /**
   * Keep our own global track of whether a lock is open for the 
   * database. We still need to handle possible locking exceptions in the 
   * event that the lock is held by a worker, but we want to avoid 
   * waiting for the full 10-second time out if we can.
   */
  private static boolean transactionInProgress = false;

  public GearsExecutor(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public boolean begin() throws Exception {
  	if(transactionInProgress) {
  		return false;
  	}
  	
    Factory factory = Factory.getInstance();
    if(factory == null) {
      throw new SqlException("Gears is not installed");
    }
    db = factory.createDatabase();
    if(db == null) {
      throw new SqlException("Could not create Gears database");
    }
    try {
    	// by including 'EXCLUSIVE' we assure that the transaction begins 
    	// immediately rather than waiting for the first non-select statement.
    	// With 'EXCLUSIVE' we don't risk getting a locked exception on subsequent 
    	// commands
      db.open(databaseName);
    	db.execute("BEGIN EXCLUSIVE TRANSACTION");
    	transactionInProgress = true;
    	return true;
    } catch(Exception e) {
    	try {
    		db.close();
    	} catch(Exception ignored) {
    	}
  		return false; // database is locked, we return false to schedule a retry
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
  		transactionInProgress = false;
  		db.close();
  	}
  }

	@Override
  public void rollback() throws Exception {
  	try {
  		db.execute("ROLLBACK TRANSACTION");
  	} finally {
  		transactionInProgress = false;
  		db.close();
  	}	  
  }
}
