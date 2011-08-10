package com.bedatadriven.rebar.sql.server.jdbc;

import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.shared.adapter.SyncTransactionAdapter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	        return toQueryResultSet(stmt);
	     } else {
	        return toUpdateResultSet(stmt);
	    }
    } finally {
    	try { stmt.close(); } catch(Exception ignored) {}
    }
  }
   

  private SqlResultSet toQueryResultSet(PreparedStatement stmt) throws SQLException {
  	 ResultSet rs = stmt.getResultSet();
  	 try {
	     ResultSetMetaData metaData = rs.getMetaData();
	
	     String[] fieldNames = new String[metaData.getColumnCount()];
	     for(int i=0;i!=fieldNames.length;++i) {
	       fieldNames[i] = metaData.getColumnName(i+1);
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
