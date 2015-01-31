package com.bedatadriven.rebar.sql.client;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common callback interface for asynchronous SQL results
 */
public abstract class SqlResultCallback {

  public static final boolean CONTINUE = false;
  public static final boolean ABORT = true;

  private static final Logger LOGGER = Logger.getLogger(SqlResultCallback.class.getName());

  public abstract void onSuccess(SqlTransaction tx, SqlResultSet results);

  /**
   * Called if there is an error while executing the statement.
   *
   * @param e the exception
   * @return {@code SqlResultCallback.ABORT} ({@code true}), if the transaction should continue, or
   * {@code SqlResultCallback.CONTINUE} ({@code false}) if the
   * transaction should be aborted and the transaction's
   * error handler called.
   */
  public boolean onFailure(SqlException e) {
    LOGGER.log(Level.SEVERE, "Sql statement failed, ABORTING (returning " + ABORT + ")", e);
    return ABORT;
  }


}
