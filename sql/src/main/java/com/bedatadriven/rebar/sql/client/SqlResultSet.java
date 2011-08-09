package com.bedatadriven.rebar.sql.client;

public interface SqlResultSet {

  /**
   * The insertId attribute must return the row ID of the row that the SQLResultSet
   * object's SQL statement inserted into the database, if the statement inserted a row.
   * If the statement inserted multiple rows, the ID of the last row must be the one returned.
   * If the statement did not insert a row, then the attribute must instead raise
   * an INVALID_ACCESS_ERR exception.
   *
   * @return the id of the last row inserted
   */
  int getInsertId();

  /**
   *
   * @return the TOTAL number of rows affected by this TRANSACTION so far.
   */
  int getRowsAffected();


  /**
   * The rows attribute must return a SQLResultSetRowList representing the rows returned,
   * in the order returned by the database. The same object must be returned each time.
   * If no rows were returned, then the object will be empty (its length will be zero).
   *
   * @return the list of rows returned by the query
   */
  SqlResultSetRowList getRows();

}