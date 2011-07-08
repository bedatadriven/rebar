package com.bedatadriven.rebar.sql.client.websql;

import com.bedatadriven.rebar.sql.client.DatabaseFactory;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;

public class WebSqlDatabaseFactory implements DatabaseFactory {

  @Override
  public SqlDatabase open(String databaseName) {
    return new DatabaseImpl(databaseName);
  }

  private static class DatabaseImpl implements SqlDatabase {

    private final WebSqlDatabase database;
    
    public DatabaseImpl(String name) {
      super();
      this.database = WebSqlDatabase.openDatabase(name, 1, name, 1024 * 1024 * 5);
    }


    @Override
    public void transaction(final SqlTransactionCallback callback) {
      database.transaction(new WebSqlTransactionCallback() {
        
        @Override
        public void onError(WebSqlException e) {
          callback.onError(e);
        }
        
        @Override
        public void begin(WebSqlTransaction tx) {
          callback.begin(new TxImpl(tx));        
        }
      });      
    }
  }
  
  
  private static class TxImpl implements SqlTransaction {

    private final WebSqlTransaction tx;
    
    public TxImpl(WebSqlTransaction tx) {
      super();
      this.tx = tx;
    }

    @Override
    public void executeSql(String statement) {
      tx.executeSql(statement);
    }

    @Override
    public void executeSql(String statement, Object[] parameters) {
      tx.executeSql(statement, parameters);
      
    }

    @Override
    public void executeSql(String statement, Object[] parameters,
        final SqlResultCallback callback) {
      tx.executeSql(statement, parameters, new WebSqlResultCallback() {
        
        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
            callback.onSuccess(TxImpl.this, new RsImpl(results));
        }
        
        @Override
        public void onFailure(WebSqlException e) {
          callback.onFailure(e);
        }
      });
    }

    @Override
    public void executeSql(String statement, final SqlResultCallback callback) {
      tx.executeSql(statement, new WebSqlResultCallback() {

        @Override
        public void onSuccess(WebSqlTransaction tx, WebSqlResultSet results) {
          callback.onSuccess(TxImpl.this, new RsImpl(results));
        }

        @Override
        public void onFailure(WebSqlException e) {
          callback.onFailure(e);
        }
        
      });
    }
  }
  
  private static class RsImpl implements SqlResultSet {
    private final WebSqlResultSet rs;

    public RsImpl(WebSqlResultSet rs) {
      super();
      this.rs = rs;
    }

    @Override
    public int getInsertId() {
      return rs.getInsertId();
    }

    @Override
    public int getRowsAffected() {
      return rs.getRowsAffected();
    }

    @Override
    public SqlResultSetRowList getRows() {
      return new RowListImpl(rs.getRows());
    }
  }
  
  private static class RowListImpl implements SqlResultSetRowList {
    private final WebSqlResultSetRowList list;

    public RowListImpl(WebSqlResultSetRowList list) {
      super();
      this.list = list;
    }

    @Override
    public int length() {
      return list.length();
    }

    @Override
    public SqlResultSetRow getRow(int index) {
      return new RowImpl(list.getRow(index));
    }
  }
  
  private static class RowImpl implements SqlResultSetRow {

    private final WebSqlResultSetRow row;
    
    public RowImpl(WebSqlResultSetRow row) {
      super();
      this.row = row;
    }

    @Override
    public String getString(String columnName) {
      return row.getString(columnName);
    }

    @Override
    public int getInt(String columnName) {
      return row.getInt(columnName);
    }

    @Override
    public double getDouble(String columnName) {
      return row.getDouble(columnName);
    }

    @Override
    public boolean isNull(String columnName) {
      return row.isNull(columnName);
    }
  } 
}
