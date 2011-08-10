package com.bedatadriven.rebar.sql.client.fn;


public class QueryAllTables implements SqlFunction0 {

	@Override
  public SqlStatement apply() {
	  return new SqlStatement("SELECT name FROM sqlite_master WHERE type='table'");
  }
	
}
