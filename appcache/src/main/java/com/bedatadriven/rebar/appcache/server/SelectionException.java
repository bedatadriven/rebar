package com.bedatadriven.rebar.appcache.server;

/**
 * An exception related to computing a properties and
 * selecting permutations;
 *
 * @author alex
 */
public class SelectionException extends RuntimeException {

  public SelectionException() {
    super();
  }

  public SelectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public SelectionException(String message) {
    super(message);
  }

  public SelectionException(Throwable cause) {
    super(cause);
  }

}
