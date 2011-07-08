package com.bedatadriven.rebar.sql.client.gears;


public class GearsTransaction {
//
//  private final Database db;
//  private final SqlTransactionCallback txCallback;
//  private final List<Statement> queue = new ArrayList<Statement>();
//  
//  private static final String[] NO_PARAMS = new String[0];
//  
//  public GearsTransaction(Database db, SqlTransactionCallback callback) {
//    this.db = db;
//    this.txCallback = callback;
//  }
//
//  @Override
//  public void executeSql(String statement) {
//    executeSql(statement, NO_PARAMS);
//  }
//
//  @Override
//  public void executeSql(String statement, Object[] parameters) {
//    executeSql(statement, toString(parameters));
//  }
//  
//  private void executeSql(String statement, String[] parameters) {
//    try {
//      db.execute(statement);
//    } catch (DatabaseException e) {
//      txCallback.onError(new SqlException(e));
//    }
//  }
//
//  @Override
//  public void executeSql(String statement, Object[] parameters,
//      WebSqlResultCallback callback) {
//    
////    try {
////      
////      
////    } catch(DatabaseException e) {
////      txCallback.onError(new SqlException(e));
////    }
//    
//  }
//
//  @Override
//  public void executeSql(String statement, WebSqlResultCallback resultCallback) {
//    // TODO Auto-generated method stub
//    
//  }
//  
//  private String[] toString(Object[] params) {
//    String ps[] = new String[params.length];
//    for(int i = 0;i!=params.length;++i) {
//      ps[i] = params.toString();
//    }
//    return ps;
//  }
//  
//  private class Statement {
//    private String sql;
//    private String[] parameters;
//    private WebSqlResultCallback callback;
//    
//    public Statement(String sql, String[] parameters) {
//      super();
//      this.sql = sql;
//      this.parameters = parameters;
//      
//    }
//  }
  
}
