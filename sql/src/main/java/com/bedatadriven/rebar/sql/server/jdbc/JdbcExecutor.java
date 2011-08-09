package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;

import java.sql.*;

class JdbcExecutor implements SyncTransactionAdapter.Executor {


  private final String connectionUrl;
  private Connection conn;

  public JdbcExecutor(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  @Override
  public void begin() throws Exception {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection(connectionUrl);
    conn.setAutoCommit(false);
  }

  @Override
  public SqlResultSet execute(String statement, Object[] params) throws Exception {
  	PreparedStatement stmt = conn.prepareStatement(statement);
  	try {
	    if(params != null) {
	      for(int i=0;i!=params.length;++i) {
	        stmt.setObject(i+1, params[i]);
	      }
	    }
	    if(stmt.execute()) {
	        return new JdbcQueryResultSet(stmt);
	     } else {
	        return new JdbcUpdateResultSet(stmt);
	    }
    } finally {
    	try { stmt.close(); } catch(Exception ignored) {}
    }
  }

  @Override
  public void commit() throws Exception {
    try {
    	conn.commit();
    } finally {
    	conn.close();
    }
  }

	@Override
  public void rollback() throws Exception {
    try {
    	conn.rollback();
    } finally {
    	conn.close();
    }
	}
  
}
