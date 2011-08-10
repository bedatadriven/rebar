package com.bedatadriven.rebar.sql.client;


public class SqlResultSet {
	
	private final int insertId;
	private final int rowsAffected;
	private final SqlResultSetRowList rows;
	
	public SqlResultSet(int insertId, int rowsAffected, SqlResultSetRowList rows) {
	  super();
	  this.insertId = insertId;
	  this.rowsAffected = rowsAffected;
	  this.rows = rows;
  }

	/**
   * The insertId attribute must return the row ID of the row that the SQLResultSet
   * object's SQL statement inserted into the database, if the statement inserted a row.
   * If the statement inserted multiple rows, the ID of the last row must be the one returned.
   * If the statement did not insert a row, then the attribute must instead raise
   * an INVALID_ACCESS_ERR exception.
   *
   * @return the id of the last row inserted
   */
  public int getInsertId() {
  	return insertId;
  }

  /**
   *
   * @return the TOTAL number of rows affected by this TRANSACTION so far.
   */
  public int getRowsAffected() {
  	return rowsAffected;
  }


  /**
   * The rows attribute must return a SQLResultSetRowList representing the rows returned,
   * in the order returned by the database. The same object must be returned each time.
   * If no rows were returned, then the object will be empty (its length will be zero).
   *
   * @return the list of rows returned by the query
   */
  public SqlResultSetRowList getRows() {
  	return rows;
  }
  
  public SqlResultSetRow getRow(int index) {
  	return getRows().get(index);
  }
  
  public Integer intResult() {
  	assertExactlyOneRow();
  	if(getRows().isEmpty()) {
  		return null;
  	}
  	
  	return getRow(0).getSingleInt();
  }
  
	private void assertExactlyOneRow() {
		if(getRows().size() > 1) {
			throw new IllegalStateException("Expected exactly zero or one rows as a result, found " + getRows().size());
		}
  }
  
}