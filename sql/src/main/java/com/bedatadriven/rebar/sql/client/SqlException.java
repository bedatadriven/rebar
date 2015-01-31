package com.bedatadriven.rebar.sql.client;

public class SqlException extends RuntimeException {

  public SqlException() {
    super();
  }

  public SqlException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public SqlException(String arg0) {
    super(arg0);
  }

  public SqlException(Throwable arg0) {
    super(arg0);
  }


}
