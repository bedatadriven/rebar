package com.bedatadriven.rebar.sql.client.query;

/**
 * Utility class that detects and caches required properties of the
 * SQL dialect in use.
 *
 * @author Alex Bertram
 */
public interface SqlDialect {

  /**
   * @param expression a valid SQL expression
   * @return Returns the SQL expression that evaluates to the year of the given  date {@code expression}
   */
  public String yearFunction(String expression);

  /**
   * @param expression a valid SQL expression
   * @return Returns the SQL expression that evaluates to the month of the given date {@code expression}
   */
  public String monthFunction(String month);

  /**
   * @param expression a valid SQL expression
   * @return Returns the SQL expression that evaluates to the quarter of the given date {@code expression}
   */
  public String quarterFunction(String column);

  /**
   * @return true if it possible to disable referential integrity for this
   * database
   */
  boolean isPossibleToDisableReferentialIntegrity();

  /**
   * @param disabled true if the referential integrity checking should be disabled
   * @return the statement which will disable or renable referential integrity checking
   */
  String disableReferentialIntegrityStatement(boolean disabled);

  /**
   * Returns the database-specific clause for limiting the size of the result list
   *
   * @param offset zero-based index of rows to start
   * @param limit  maximum number of rows to return, or zero for no limit
   * @return
   */
  String limitClause(int offset, int limit);

  boolean isMySql();
}
