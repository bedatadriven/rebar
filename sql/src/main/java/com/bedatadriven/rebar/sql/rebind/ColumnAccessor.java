package com.bedatadriven.rebar.sql.rebind;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

abstract class ColumnAccessor {
	public abstract Object get(SqlResultSetRow row, String columnName);
}