package com.bedatadriven.rebar.sql.client;



public interface SqlResultSetRowList extends Iterable<SqlResultSetRow> {

  int size();

  SqlResultSetRow get(int index);

  boolean isEmpty();
  
}
