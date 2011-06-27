package com.bedatadriven.rebar.sql.server.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;

public class SyncSqlTransactionAdapter implements SqlTransaction {

  public static interface Executor {
    SqlResultSet execute(String statement, Object[] params) throws SqlException;

    void commit();
  }
  
  private List<Statement> queue = new ArrayList<Statement>();
  private int nextStatementIndex = 0;
  
  private SqlTransactionCallback callback;
  private Executor executor;
  
  public SyncSqlTransactionAdapter(Executor executor, SqlTransactionCallback callback) {
    this.executor = executor;
    this.callback = callback;
    
    // 4. If the transaction callback is not null, queue a task to invoke the transaction callback with the
    //    aforementioned SQLTransaction object as its only argument, and wait for that task to be run.
    // 
    // 5. If the callback raised an exception, jump to the last step.
    
    try {
      callback.begin(this);
    } catch(Exception e) {
      errorInCallback(e);
    }
    
    processNextStatement();
  }

  private void errorInCallback(Exception e) {
    callback.onError(new SqlException(e));
  }
 
  private void processNextStatement() {
    if(nextStatementIndex >= queue.size()) {
      commitTransaction();
    } else {
      queue.get(nextStatementIndex++).execute();
    }
  }
  
  
  private void commitTransaction() {
    try {
      executor.commit();
    } catch(Exception e) {
      errorInCallback(e);
    }
  }

  @Override
  public void executeSql(String statement) {
    queue.add(new Statement(statement));
  }

  @Override
  public void executeSql(String statement, Object[] parameters) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void executeSql(String statement, Object[] parameters,
      SqlResultCallback callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void executeSql(String statement, SqlResultCallback resultCallback) {
    // TODO Auto-generated method stub
    
  }
  
  private class Statement {
    private String statement;
    private Object[] params;
    private SqlResultCallback callback;
    
    
    public Statement(String statement) {
      super();
      this.statement = statement;
    }
    
    public void execute() {
      SqlResultSet result;
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
          callback.onSuccess(SyncSqlTransactionAdapter.this, results);
        } catch(Exception e) {
          errorInCallback(e);
          return;
        }
        processNextStatement();
      }
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
      return callback.onFailure(e);
    }
  }

}
