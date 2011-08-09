package com.bedatadriven.rebar.dao.model;

public class ModelException extends RuntimeException {

  public ModelException() {
    super();
  }

  public ModelException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModelException(String message) {
    super(message);
  }

  public ModelException(Throwable cause) {
    super(cause);
  }

}
