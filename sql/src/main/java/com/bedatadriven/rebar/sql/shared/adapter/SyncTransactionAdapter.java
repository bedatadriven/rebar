package com.bedatadriven.rebar.sql.shared.adapter;


import com.allen_sauer.gwt.log.client.Log;
import com.bedatadriven.rebar.sql.client.*;
import com.google.gwt.core.client.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Adapts a synchronous database API to the WebSql-style asynchronous
 * transaction API
 */
public class SyncTransactionAdapter implements SqlTransaction {

  private Logger logger = Logger.getLogger("NameOfYourLogger");


  public static interface Executor {
    SqlResultSet execute(String statement, Object[] params) throws Exception;
    void begin() throws Exception;
    void commit() throws Exception;
    void rollback() throws Exception;
  }


  private List<Statement> queue = new ArrayList<Statement>();
  private int nextStatementIndex = 0;

  private SqlTransactionCallback callback;
  private Executor executor;
  private Scheduler scheduler;

  public SyncTransactionAdapter(Executor executor, Scheduler scheduler, SqlTransactionCallback callback) {
    this.executor = executor;
    this.callback = callback;
    this.scheduler = scheduler;

    Log.debug("SyncTransactionAdapter: Starting Async Transaction...");

    // ask our implementation to set up

    try {
      executor.begin();
    } catch(Exception e) {
      Log.error("SyncTransactionAdapter: Exception thrown during executor.begin()", e);

      callback.onError(new SqlException(e));
    }

    // 4. If the transaction callback is not null, queue a task to invoke the transaction callback with the
    //    aforementioned SQLTransaction object as its only argument, and wait for that task to be run.
    //
    // 5. If the callback raised an exception, jump to the last step.

    try {
      callback.begin(this);
    } catch(Exception e) {
      Log.error("SyncTransactionAdapter: Exception thrown in transaction callback", e);

      errorInCallback(e);
    }

    processNextStatement();
  }

  private void errorInCallback(Exception e) {
  	try {
  		Log.error("SyncTransactionAdapter: Rolling back transaction");
  		executor.rollback();
  	} catch(Exception rollbackException) {
  		Log.error("SyncTransactionAdapter: Exception while rolling back transaction", rollbackException);
  	}
    callback.onError(new SqlException(e));
  }

  private void processNextStatement() {
    if(nextStatementIndex >= queue.size()) {
      commitTransaction();
    } else {
      scheduler.scheduleFinally(new Scheduler.ScheduledCommand() {
        @Override
        public void execute() {
          queue.get(nextStatementIndex++).execute();
        }
      });
    }
  }


  private void commitTransaction() {
    Log.debug("SyncTransactionAdapter: All queued statements have been executed, committing");

    try {
      executor.commit();
    } catch(Exception e) {
      Log.error("SyncTransactionAdapter: Exception thrown while committing", e);

      errorInCallback(e);
      return;
    }
    
    Log.debug("SyncTransactionAdapter: Commit succeeded.");
    
    // everything worked! let the caller know
    callback.onSuccess();
    
  }

  @Override
  public void executeSql(String statement) {
    queue.add(new Statement(statement, null, null));
  }

  @Override
  public void executeSql(String statement, Object[] parameters) {
    queue.add(new Statement(statement, parameters, null));

  }

  @Override
  public void executeSql(String statement, Object[] parameters,
      SqlResultCallback callback) {
    queue.add(new Statement(statement, parameters, callback));
  }

  @Override
  public void executeSql(String statement, SqlResultCallback resultCallback) {
    queue.add(new Statement(statement, null, resultCallback));
  }

  private class Statement {
    private String statement;
    private Object[] params;
    private SqlResultCallback callback;

    private Statement(String statement, Object[] params, SqlResultCallback callback) {
      this.statement = statement;
      this.params = params;
      this.callback = callback;
    }

    public void execute() {

      Log.debug("SyncTransactionAdapter: Executing statement '" + statement + "' with parameters " +
          Arrays.toString(params));

      try {
        handleStatementSuccess(executor.execute(statement, params));
      } catch(SqlException e) {
        handleStatementError(e);
      } catch(Exception e) {
        errorInCallback(e);
      }
    }

    private void handleStatementSuccess(SqlResultSet results) {
      if(callback != null) {
        try {
          callback.onSuccess(SyncTransactionAdapter.this, results);
        } catch(Exception e) {
          errorInCallback(e);
          return;
        }
      }
      processNextStatement();
    }

    private void handleStatementError(SqlException e) {
      if(shouldContinue(e)) {
        processNextStatement();
      } else {
        errorInCallback(e);
      }
    }

    private boolean shouldContinue(SqlException e) {
      if(callback == null) {
        return false;
      }
      return callback.onFailure(e) == SqlResultCallback.CONTINUE;
    }
  }

}
