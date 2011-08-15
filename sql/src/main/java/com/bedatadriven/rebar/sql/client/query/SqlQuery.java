package com.bedatadriven.rebar.sql.client.query;

/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Lightweight DSL for building SQL queries.
 */
public class SqlQuery {


	protected AsyncCallback<?> errorHandler;
	
	protected final StringBuilder columnList = new StringBuilder();
	protected final StringBuilder tableList = new StringBuilder();
	protected final StringBuilder whereClause = new StringBuilder();
	protected final StringBuilder orderByClause = new StringBuilder();
	protected final List<Object> parameters = new ArrayList<Object>();

	private String limitClause = "";

	public SqlQuery() {
	}

	public static SqlQuery select(String... columns) {
		 SqlQuery builder = new SqlQuery();
		 for(String column : columns) {
			 builder.appendColumn(column);
		 }
		 return builder;
 }
	
	/**
	 * Appends a table list to the {@code FROM} clause
	 * @param fromClause valid SQL table list, can include joins
	 */
	public SqlQuery from(String fromClause) {
		tableList.append(fromClause);
		return this;
	}
	
	public SqlQuery from(String fromClause, String alias) {
		tableList.append(fromClause).append(" ").append(alias);
		return this;
	}

	/**
	 * Appends a left join to the {@code FROM} clause
	 * @param tableName
	 * @return
	 */
	public JoinBuilder leftJoin(String tableName) {
		tableList.append(" LEFT JOIN ").append(tableName);
		return new JoinBuilder();
	}
	
	/**
	 * Appends a left join to the {@code FROM} clause
	 * @param tableName
	 * @return
	 */
	public JoinBuilder leftJoin(String tableName, String alias) {
		tableList.append(" LEFT JOIN ").append(tableName).append(" ").append(alias);
		return new JoinBuilder();
	}

	/**
	 * Appends an inner join to the {@code FROM} clause
	 * @param tableName
	 * @return
	 */
	public JoinBuilder innerJoin(String tableName) {
		tableList.append(" INNER JOIN ").append(tableName);
		return new JoinBuilder();
	}

	/**
	 * Appends an inner join to the {@code FROM} clause
	 * @param tableName
	 * @return
	 */
	public JoinBuilder innerJoin(String tableName, String alias) {
		tableList.append(" INNER JOIN ").append(tableName).append(" ").append(alias);
		return new JoinBuilder();
	}

	
	/**
	 * Appends a left join to derived table to the {@code FROM} clause
	 */
	public JoinBuilder leftJoin(SqlQuery derivedTable, String alias) {
		parameters.addAll(derivedTable.parameters);
		tableList.append(" LEFT JOIN (")
		.append(derivedTable.sql())
		.append(")")
		.append(" AS ")
		.append(alias);

		return new JoinBuilder();
	}

	/**
	 * Appends a column a comma-separated list of columns to the select list.
	 *
	 */
	public SqlQuery appendColumn(String expr) {
		if(columnList.length() != 0) {
			columnList.append(", ");
		}
		columnList.append(expr);
		return this;
	}
	
	public SqlQuery appendColumn(String expr, String alias) {
		return appendColumn(expr + " " + alias);
	}
	
	public SqlQuery appendColumns(String... exprs) {
		for(String expr : exprs) {
			appendColumn(expr);
		}
		return this;
	}

	public SqlQuery orderBy(String expr) {
		if(orderByClause.length() > 0) {
			orderByClause.append(", ");
		}
		orderByClause.append(expr);
		return this;
	}
	
	public SqlQuery orderBy(String expr, boolean ascending) {
		orderBy(expr);
		if(!ascending) {
			orderByClause.append(" DESC");
		}
		return this;
	}
	

	public SqlQuery orderBy(SqlQuery subQuery, boolean ascending) {
		assert subQuery.parameters.isEmpty();
		
	  return orderBy("(" + subQuery.sql() + ")", ascending);
  }

	public void setLimitClause(String clause) {
		this.limitClause = clause;
	}

	public WhereClauseBuilder where(String expr) {
		if(whereClause.length() > 0) {
			whereClause.append(" AND ");
		}
		whereClause.append(expr);
		return new WhereClauseBuilder();
	}
	
	public SqlQuery appendParameter(Object param) {
		parameters.add(param);
		return this;
	}


	public SqlQuery whereTrue(String expr) {
		if(whereClause.length() > 0) {
			whereClause.append(" AND ");
		}
		whereClause.append(expr);
		return this;
	}

	public SqlQuery and(String expr) {
		whereClause.append(" AND (").append(expr).append(") ");
		return this;
	}
	
	public String sql() {
		StringBuilder sql = new StringBuilder("SELECT ")
		.append(columnList.toString())
		.append(" FROM ")
		.append(tableList.toString());

		if(whereClause.length() > 0) {
			sql.append(" WHERE ")
			.append(whereClause.toString());
		}
		if(orderByClause.length() > 0) {
			sql.append(" ORDER BY ")
			.append(orderByClause.toString());
		}
		sql.append(" ")
		.append(limitClause);

		return sql.toString();
	}
	
	public Object[] parameters() {
		return parameters.toArray(new Object[parameters.size()]);
	}
	
	public SqlQuery delegateErrorsTo(AsyncCallback<?> callback) {
	  this.errorHandler = callback;
	  return this;
  }
	

	public void execute(SqlDatabase database, final SqlResultCallback callback) {
		database.transaction(new SqlTransactionCallback() {
			
			@Override
			public void begin(SqlTransaction tx) {
				execute(tx, callback);
			}

			@Override
      public void onError(SqlException e) {
				if(errorHandler != null) {
					errorHandler.onFailure(e);
				}
			}
		});
	}
	
	public void execute(SqlTransaction tx, final SqlResultCallback callback) {
		tx.executeSql(sql(), parameters(), callback);
	}
	
	public class WhereClauseBuilder {

		public SqlQuery in(Collection<?> ids) {
			if (ids.isEmpty()) {
				throw new IllegalArgumentException("Cannot match against empty list.");
			}
			if (ids.size() == 1) {
				whereClause.append(" = ?");
			} else {
				whereClause.append(" IN (? ");
				for (int i = 1; i < ids.size(); ++i) {
					whereClause.append(", ?");
				}
				whereClause.append(")");
			}
			parameters.addAll(ids);
			return SqlQuery.this;
		}

		public SqlQuery in(SqlQuery subquery) {
			whereClause.append(" IN (")
			.append(subquery.sql())
			.append(") ");

			parameters.addAll(subquery.parameters);

			return SqlQuery.this;
		}

		public SqlQuery equalTo(Object value) {
			whereClause.append(" = ? ");
			parameters.add(value);

			return SqlQuery.this;
		}

		public SqlQuery isNull() {
			whereClause.append(" IS NULL ");
			return SqlQuery.this;
    }
	}

	public class JoinBuilder {
		public SqlQuery on(String expr) {
			tableList.append(" ON (").append(expr).append(") ");
			return SqlQuery.this;
		}
	}

}
