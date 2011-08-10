package com.bedatadriven.rebar.sql.client.fn;

public class SqlStatement {

	private String statement;
	private Object[] params;
	
	public SqlStatement(String statement, Object[] params) {
	  super();
	  this.statement = statement;
	  this.params = params;
  }

	public SqlStatement(String statement) {
		this.statement = statement;
	}

	
	
}
