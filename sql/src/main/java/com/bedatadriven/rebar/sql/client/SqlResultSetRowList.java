package com.bedatadriven.rebar.sql.client;


public interface SqlResultSetRowList {

  int length();
  
  SqlResultSetRow getRow(int index);

}
