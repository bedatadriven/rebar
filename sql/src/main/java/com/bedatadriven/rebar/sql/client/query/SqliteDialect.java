package com.bedatadriven.rebar.sql.client.query;


public class SqliteDialect implements SqlDialect {

  // TODO: implement date/time functions for pivot tables...
  // http://www.sqlite.org/cvstrac/wiki?p=DateAndTimeFunctions

  public static final SqlDialect INSTANCE = new SqliteDialect();

	@Override
  public String yearFunction(String expression) {
      throw new UnsupportedOperationException();
  }

  @Override
  public String monthFunction(String month) {
      throw new UnsupportedOperationException();
  }

  @Override
  public String quarterFunction(String column) {
      throw new UnsupportedOperationException();
  }

  @Override
  public boolean isPossibleToDisableReferentialIntegrity() {
      return false;
  }

  @Override
  public String disableReferentialIntegrityStatement(boolean disabled) {
      throw new UnsupportedOperationException();
  }

  @Override
  public String limitClause(int offset, int limit) {
      return "LIMIT " + limit + " OFFSET " + offset;
  }
  

  @Override
  public boolean isMySql() {
    return false;
  }
}

