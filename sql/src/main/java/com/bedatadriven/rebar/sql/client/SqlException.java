package com.bedatadriven.rebar.sql.client;

public class SqlException extends RuntimeException {

  public SqlException() {
    super();
  }

  public SqlException(String message, Throwable cause) {
    super(message, cause);
  }

  public SqlException(String message) {
    super(message);
  }

  public SqlException(Throwable cause) {
    super(cause);
  }
  
  

}
