package com.bedatadriven.rebar.sql.shared.adapter;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Adapts a synchronous database API to the WebSql-style asynchronous
 * transaction API
 */
public class SyncTransactionAdapter implements SqlTransaction {

  private static int nextTxId = 1;
  private boolean committed = false;
  private boolean manualCommitting = false;
  
  private static final Logger LOGGER = Logger.getLogger(SyncTransactionAdapter.class.getName());
  
  public static interface Executor {
    SqlResultSet execute(String statement, Object[] params) throws Exception;
    boolean begin() throws Exception;
    void commit() throws Exception;
    void rollback() throws Exception;
  }


  private LinkedList<ScheduledCommand> queue = new LinkedList<ScheduledCommand>();

  private SqlTransactionCallback callback;
  private Executor executor;
  private Scheduler scheduler;
  private int id;

  public SyncTransactionAdapter(Executor executor, Scheduler scheduler, SqlTransactionCallback callback) {
    this.executor = executor;
    this.callback = callback;
    this.scheduler = scheduler;
    this.id = nextTxId++;

    LOGGER.fine("SyncTx[" + id + "]: Starting Async Transaction...");

    scheduleBeginTransaction();
  }
  

	public void withManualCommitting() {
		this.manualCommitting = true;
  }

	private void beginTransaction() {
	  try {
      if(!executor.begin()) {

      	LOGGER.warning("SyncTx[" + id + "]: Unable to acquire tx lock, rescheduling");

      	scheduleBeginTransaction();
      	return;
      }
    } catch(Exception e) {
    	LOGGER.log(Level.SEVERE, "SyncTx[" + id + "]: Exception thrown during executor.begin()", e);

      callback.onError(new SqlException(e));
      return;
    }

    // 4. If the transaction callback is not null, queue a task to invoke the transaction callback with the
    //    aforementioned SQLTransaction object as its only argument, and wait for that task to be run.
    //
    // 5. If the callback raised an exception, jump to the last step.

    try {
      callback.begin(this);
    } catch(Throwable e) {
      LOGGER.log(Level.SEVERE, "SyncTx[" + id + "]: Exception thrown in transaction callback", e);

      errorInCallback(e);
      return;
    }

    scheduleProcess();
  }

	
  private void scheduleBeginTransaction() {

  	scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				beginTransaction();
			}
		});
  }

	private void errorInCallback(Throwable e) {
  	try {
  		LOGGER.log(Level.SEVERE, "SyncTx[" + id + "]: Rolling back transaction");
  		executor.rollback();
  	} catch(Exception rollbackException) {
  		LOGGER.log(Level.WARNING, "SyncTx[" + id + "]: Exception while rolling back transaction", rollbackException);
  	}
    callback.onError(new SqlException(e));
  }

  private void scheduleProcess() {
  	scheduler.scheduleFinally(new Scheduler.ScheduledCommand() {
      @Override
      public void execute() {
      	 if(queue.isEmpty()) {
      		 if(!committed && !manualCommitting) {
	      		 committed = true;
	           commitTransaction();
      		 }
         } else {
           queue.poll().execute();
           scheduleProcess();
         }
      }
    });
   
  }

  public void process() {
  	scheduleProcess();
  }

  public void commitTransaction() {
    LOGGER.fine("SyncTx[" + id + "]: All queued statements have been executed, committing");

    try {
      executor.commit();
    } catch(Exception e) {
      LOGGER.log(Level.SEVERE, "SyncTx[" + id + "]: Exception thrown while committing", e);

      errorInCallback(e);
      return;
    }
    
    LOGGER.fine("SyncTx[" + id + "]: Commit succeeded.");
    
    // everything worked! let the caller know
    callback.onSuccess();
    
  }

  @Override
  public void executeSql(String statement) {
    enqueue(new Statement(statement, null, null));
  }

  private void enqueue(ScheduledCommand statement) {
  	if(committed) {
  		throw new IllegalStateException("transaction has already started committed, no new statements can be queued");
  	}
	  queue.add(statement);
	  scheduleProcess();
  }

	@Override
  public void executeSql(String statement, Object[] parameters) {
		enqueue(new Statement(statement, parameters, null));
  }

  @Override
  public void executeSql(String statement, Object[] parameters,
      SqlResultCallback callback) {
   enqueue(new Statement(statement, parameters, callback));
  }

  @Override
  public void executeSql(String statement, SqlResultCallback resultCallback) {
  	enqueue(new Statement(statement, null, resultCallback));
  }

  private class Statement implements ScheduledCommand {
    private String statement;
    private Object[] params;
    private SqlResultCallback callback;

    private Statement(String statement, Object[] params, SqlResultCallback callback) {
      this.statement = statement;
      this.params = params;
      this.callback = callback;
    }

    public void execute() {

      LOGGER.fine("SyncTx[" + id + "]: Executing statement '" + statement + "' with parameters " +
          Arrays.toString(params));

      try {
        handleStatementSuccess(executor.execute(statement, params));
      } catch(SqlException e) {
        handleStatementError(e);
      } catch(Exception e) {
        errorInCallback(e);
      }
    }

    private void handleStatementSuccess(final SqlResultSet results) {
      if(callback != null) {
      		// If the statement has a result set callback that is not null, queue a task to invoke it with the
      	  // SQLTransaction object as its first argument and the new SQLResultSet object as its second argument, 
      	  // and wait for that task to be run.
      	
        	SyncTransactionAdapter.this.enqueue(new ScheduledCommand() {
						@Override
						public void execute() {
			        try {
			        	callback.onSuccess(SyncTransactionAdapter.this, results);
			          scheduleProcess();
			        } catch(Exception e) {
			          errorInCallback(e);
			        }
						}
					});
      } else {
      	scheduleProcess();
      }
    }

    private void handleStatementError(SqlException e) {
      if(shouldContinue(e)) {
        scheduleProcess();
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
