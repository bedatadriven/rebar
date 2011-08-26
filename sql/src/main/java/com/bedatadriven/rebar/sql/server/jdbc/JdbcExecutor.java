package com.bedatadriven.rebar.sql.server.jdbc;

import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class JdbcExecutor implements SyncTransactionAdapter.Executor {
  
  protected Connection conn;

  @Override
  public final boolean begin() throws Exception {
    conn = openConnection();
    
    try {
	    boolean available = doBeginTransaction();
	    if(!available) {
	    	closeConnectionIgnoringAnyExceptions();
	    }
	    return available;
    
    } catch(SQLException e) {
    	closeConnectionIgnoringAnyExceptions();
    	throw e;
    }
  }

	protected abstract Connection openConnection() throws Exception;
 

  private void closeConnectionIgnoringAnyExceptions() {
  	try {
  		conn.close();
  	} catch(SQLException ignored) {
  		// ignore
  	}
  }
  
  
  @Override
  public final SqlResultSet execute(String statement, Object[] params) throws Exception {
  	PreparedStatement stmt = prepareStatement(statement);
  	try {
	    if(params != null) {
	      for(int i=0;i!=params.length;++i) {
	      	if(params[i] instanceof java.util.Date) {
	      		stmt.setString(i+1, SqliteDates.format((java.util.Date)params[i]));
	      	} else {
	      		stmt.setObject(i+1, params[i]);
	      	}
	      }
	    }
	    if(stmt.execute()) {
	        return toQueryResultSet(stmt);
	     } else {
	        return toUpdateResultSet(stmt);
	    }
  	} catch(Exception e) {
  		Log.debug("Exception thrown while executing statement: " + statement, e);
  		throw e;
    } finally {
    	try { stmt.close(); } catch(Exception ignored) {}
    }
  }

	private PreparedStatement prepareStatement(String statement)
      throws SQLException {
		// workaround for incomplete SQL implementation
		// TODO: move to sqlite-specific subclass
		if(conn.getClass().getName().equals("org.sqlite.Conn")) {
			return conn.prepareStatement(statement);
		} else {
			return conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
		}
  }
  
  private SqlResultSet toQueryResultSet(PreparedStatement stmt) throws SQLException {
  	 ResultSet rs = stmt.getResultSet();
  	 try {
	     ResultSetMetaData metaData = rs.getMetaData();
	
	     String[] fieldNames = new String[metaData.getColumnCount()];
	     for(int i=0;i!=fieldNames.length;++i) {
	       fieldNames[i] = metaData.getColumnLabel(i+1);
	     }
	
	     List<SqlResultSetRow> rows = new ArrayList<SqlResultSetRow>();
	     while(rs.next()) {
	       rows.add(new JdbcRow(rs, fieldNames));
	     }
	
	     return new SqlResultSet(-1, 0, new JdbcResultSetRowList(rows));
  	 } finally {
  		 try { rs.close(); } catch(Exception ignored) {}
  	 }
  }

	private SqlResultSet toUpdateResultSet(PreparedStatement stmt) throws SQLException {
    int rowsAffected = stmt.getUpdateCount();
    int insertId = -1;
    ResultSet rs = stmt.getGeneratedKeys();
    try {
      if(rs.next()) {
        insertId = rs.getInt(1);
        if(rs.wasNull()) {
          insertId = -1;
        }
      }
      return new SqlResultSet(insertId, rowsAffected, new JdbcResultSetRowList(Collections.<SqlResultSetRow>emptyList()));
    } finally {
      try { rs.close(); } catch(Exception ignored) {}
    }
  }

	@Override
  public final void commit() throws Exception {
    try {
    	doCommit();
    } finally {
    	conn.close();
    }
  }

	protected boolean doBeginTransaction() throws SQLException {
		conn.setAutoCommit(false);
		return true;
  }
	
	protected void doCommit() throws SQLException {
		conn.commit();
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
