package com.bedatadriven.rebar.sql.client.query;

public class MySqlDialect implements SqlDialect {
	
	
  public static final SqlDialect INSTANCE = new MySqlDialect();

	@Override
  public String yearFunction(String column) {
      return "YEAR(" + column + ")";
  }

  @Override
  public String monthFunction(String column) {
      return "MONTH(" + column + ")";
  }

  @Override
  public String quarterFunction(String column) {
      return "QUARTER(" + column + ")";
  }


  @Override
  public boolean isPossibleToDisableReferentialIntegrity() {
      return true;
  }

  @Override
  public String disableReferentialIntegrityStatement(boolean disabled) {
      return "SET foreign_key_checks = " + (disabled ? "0" : "1");
  }

  @Override
  public String limitClause(int offset, int limit) {
      return new StringBuilder("LIMIT ")
              .append(offset).append(',').append(limit == 0 ? Integer.MAX_VALUE : limit)
              .toString();
  }
}
