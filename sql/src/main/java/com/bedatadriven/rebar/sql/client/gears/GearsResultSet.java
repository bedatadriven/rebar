package com.bedatadriven.rebar.sql.client.gears;

import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

class GearsResultSet implements SqlResultSet {

  private final GearsResultSetRowList rowList;
  private final int insertId;
  private final int rowsAffected;

  public GearsResultSet(Database db, ResultSet rs) throws DatabaseException {
    rowList = new GearsResultSetRowList(rs);
    insertId = db.getLastInsertRowId();
    rowsAffected = db.getRowsAffected();
  }

  @Override
  public int getInsertId() {
    if(insertId == 0) {
      throw new SqlException("No rows were inserted");
    }
    return insertId;
  }

  @Override
  public int getRowsAffected() {
    return rowsAffected;
  }

  @Override
  public SqlResultSetRowList getRows() {
    return rowList;
  }
}
