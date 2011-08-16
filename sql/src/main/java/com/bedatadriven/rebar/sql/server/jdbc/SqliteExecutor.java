package com.bedatadriven.rebar.sql.server.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteExecutor extends JdbcExecutor {

	private String connectionUrl;

	public SqliteExecutor(String connectionUrl) {
	  this.connectionUrl = connectionUrl;
  }
	
	@Override
  protected Connection openConnection() throws ClassNotFoundException, SQLException {
	  Class.forName("org.sqlite.JDBC");
    return DriverManager.getConnection(connectionUrl);
  }

	@Override
  protected boolean doBeginTransaction() throws SQLException {
		try {
			Statement stmt = conn.createStatement();
		  stmt.execute("BEGIN EXCLUSIVE TRANSACTION");
		  stmt.close();
		  
		  return true;
	    
    } catch(SQLException e) {
    	if(e.getMessage().contains("[SQLITE_BUSY]")) {
    		return false; // database is locked; attempt will be rescheduled
    	} else {
    		throw e; // some other fatal error
    	}
    }
  }

	@Override
  protected void doCommit() throws SQLException {
		 Statement stmt = conn.createStatement();
		 stmt.execute("END TRANSACTION");
		 stmt.close();
  }

	@Override
  public void rollback() throws Exception {
		 Statement stmt = conn.createStatement();
		 stmt.execute("ROLLBACK TRANSACTION");
		 stmt.close();
  }
	
	
}
