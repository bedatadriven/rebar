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
import java.util.Date;
import java.util.List;

import com.bedatadriven.rebar.async.AsyncFunction;
import com.bedatadriven.rebar.sql.client.SqlDatabase;
import com.bedatadriven.rebar.sql.client.SqlException;
import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlResultSetRowList;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.SqlTransactionCallback;
import com.google.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Lightweight DSL for building SQL queries.
 */
public class SqlQuery extends AsyncFunction<SqlTransaction, SqlResultSetRowList> {

	protected AsyncCallback<?> errorHandler;

	/**
	 * Set of keywords like "DISTINCT" or "SQL_CALC_FOUND_ROWS" that follow 
	 * the SELECT keyword and precede the column list
	 */
	protected final StringBuilder keywords = new StringBuilder();
	protected final StringBuilder columnList = new StringBuilder();
	protected final StringBuilder tableList = new StringBuilder();
	protected final StringBuilder whereClause = new StringBuilder();
	protected final StringBuilder orderByClause = new StringBuilder();
	protected final StringBuilder groupByClause = new StringBuilder();
	protected final List<Object> parameters = new ArrayList<Object>();

	private String limitClause = "";

	public SqlQuery() {
	}

	public static SqlQuery select(String... columns) {
		SqlQuery builder = new SqlQuery();
		for (String column : columns) {
			builder.appendColumn(column);
		}
		return builder;
	}

	public static SqlQuery selectDistinct() {
		SqlQuery builder = new SqlQuery();
		builder.keywords.append(" DISTINCT ");
		return builder;
	}
	
	public static SqlQuery selectAll() {
		SqlQuery builder = new SqlQuery();
		builder.columnList.append("*");
		return builder;
	}

	public static SqlQuery selectSingle(String expr) {
		SqlQuery builder = new SqlQuery();
		builder.columnList.append(expr);
		return builder;
	}

	public SqlQuery appendKeyword(String keyword) {
		keywords.append(" ").append(keyword);
		return this;
	}
	
	/**
	 * Appends a table list to the {@code FROM} clause
	 * 
	 * @param fromClause
	 *            valid SQL table list, can include joins
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
	 * 
	 * @param tableName
	 * @return
	 */
	public JoinBuilder leftJoin(String tableName) {
		tableList.append(" LEFT JOIN ").append(tableName);
		return new JoinBuilder();
	}

	/**
	 * Appends a left join to the {@code FROM} clause
	 * 
	 * @param tableName
	 * @return
	 */
	public JoinBuilder leftJoin(String tableName, String alias) {
		tableList.append(" LEFT JOIN ").append(tableName).append(" ")
				.append(alias);
		return new JoinBuilder();
	}

	/**
	 * Appends an inner join to the {@code FROM} clause
	 * 
	 * @param tableName
	 * @return
	 */
	public JoinBuilder innerJoin(String tableName) {
		tableList.append(" INNER JOIN ").append(tableName);
		return new JoinBuilder();
	}

	/**
	 * Appends an inner join to the {@code FROM} clause
	 * 
	 * @param tableName
	 * @return
	 */
	public JoinBuilder innerJoin(String tableName, String alias) {
		tableList.append(" INNER JOIN ").append(tableName).append(" ")
				.append(alias);
		return new JoinBuilder();
	}

	/**
	 * Appends a left join to derived table to the {@code FROM} clause
	 */
	public JoinBuilder leftJoin(SqlQuery derivedTable, String alias) {
		parameters.addAll(derivedTable.parameters);
		tableList.append(" LEFT JOIN (").append(derivedTable.sql()).append(")")
				.append(" AS ").append(alias);

		return new JoinBuilder();
	}

	/**
	 * Appends a column a comma-separated list of columns to the select list.
	 * 
	 */
	public SqlQuery appendColumn(String expr) {

		if (expr.contains(",")) {
			throw new IllegalArgumentException(
					"Cannot accept a column name with a comma. Hint: "
							+ "You can no longer pass \"a,b,c\" as an argument you should be passing \"a\", \"b\", \"c\"");
		}

		if (expr.contains("*")) {
			throw new IllegalArgumentException(
					"Cannot accept a column name with a '*'. Hint: "
							+ "use SqlQuery.selectAll() instead");
		}

		if (expr.contains(")")) {
			throw new IllegalArgumentException(
					"You must provide an explicit alias for complicated expressions: "
							+ expr);
		}

		// unfortunately the API mandated by WebSQL uses case-sensitive
		// names to lookup results.
		// to smooth over differences between the way MySQL and Sqlite
		// handle casing of implicit column aliases, we will always
		// provide an explicit alias with the same casing as provided to expr

		int dot = expr.indexOf('.');
		if (dot == -1) {
			return appendColumn(expr, expr);
		} else {
			String alias = expr.substring(dot + 1);
			return appendColumn(expr, alias);
		}
	}
	
	/**
	 * Appends the "*" to the column list
	 */
	public SqlQuery appendAllColumns() {
		if (columnList.length() != 0) {
			columnList.append(", ");
		}
		columnList.append("*");
		return this;
	}

	/**
	 * Appends an expression with an explicit alias to the column list
	 * @param expr an sql expression
	 * @param alias the query alias
	 */
	public SqlQuery appendColumn(String expr, String alias) {
		if (columnList.length() != 0) {
			columnList.append(", ");
		}
		columnList.append(expr).append(" ").append(alias);

		return this;
	}

	/**
	 * Appends zero or more expressions to the column list
	 * @param exprs a list of SQL expressions
	 */
	public SqlQuery appendColumns(String... exprs) {
		for (String expr : exprs) {
			appendColumn(expr);
		}
		return this;
	}
	
	/**
	 * Appends a subquery to the column list, with the given {@code alias}
	 */
	public SqlQuery appendColumn(SqlQuery subQuery, String alias) {
		if (columnList.length() != 0) {
			columnList.append(", ");
		}
		columnList.append("(").append(subQuery.sql())
			.append(")").append(" ").append(alias);
		parameters.addAll(subQuery.parameters);
		return this;
	}

	/**
	 * Adds an expression to the {@code ORDER BY} clause
	 */
	public SqlQuery orderBy(String expr) {
		if (orderByClause.length() > 0) {
			orderByClause.append(", ");
		}
		orderByClause.append(expr);
		return this;
	}

	/**
	 * Adds an expression to the {@code ORDER BY} clause
	 * @param ascending {@code true} for ascending, {@code false} for descending
	 */
	public SqlQuery orderBy(String expr, boolean ascending) {
		orderBy(expr);
		if (!ascending) {
			orderByClause.append(" DESC");
		}
		return this;
	}

	/**
	 * Adds an expression to the {@code GROUP BY} clause
	 */
	public SqlQuery groupBy(String expr) {
		if (groupByClause.length() > 0) {
			groupByClause.append(", ");
		}
		groupByClause.append(expr);
		return this;
	}

	public SqlQuery orderBy(SqlQuery subQuery, boolean ascending) {
		assert subQuery.parameters.isEmpty();

		return orderBy("(" + subQuery.sql() + ")", ascending);
	}

	public SqlQuery setLimitClause(String clause) {
		this.limitClause = clause;
		return this;
	}

	public WhereClauseBuilder onlyWhere(String expr) {
		whereClause.append(expr);
		return new WhereClauseBuilder();
	}

	public WhereClauseBuilder whereLikes(String expr) {
		whereClause.append(expr);
		return new WhereClauseBuilder(expr);
	}

	public WhereClauseBuilder where(String expr) {
		if (whereClause.length() > 0) {
			whereClause.append(" AND ");
		}
		whereClause.append(expr);
		return new WhereClauseBuilder();
	}

	public SqlQuery appendParameter(Object param) {
		parameters.add(param);
		return this;
	}

	private SqlQuery appendLikeParameter(String param) {
		parameters.add("%" + param + "%");
		return this;
	}

	public SqlQuery whereTrue(String expr) {
		if (whereClause.length() > 0) {
			whereClause.append(" AND ");
		}
		whereClause.append(expr);
		return this;
	}

	public SqlQuery and(String expr) {
		whereClause.append(" AND (").append(expr).append(") ");
		return this;
	}
	
	public WhereClauseBuilder or(String expr) {
		if(whereClause.length() > 0) {
			whereClause.append(" OR ");
		}
		whereClause.append(expr);
		return new WhereClauseBuilder();
		
	}
	
	public SqlQuery or() {
		whereClause.append(" OR ");
		return this;
	}

	public String sql() {
		if (columnList.length() == 0) {
			throw new IllegalStateException("No columns have been specified");
		}
		if (tableList.length() == 0) {
			throw new IllegalStateException("No FROM clause has been provided");
		}

		// append(StringBuilder) fails bizarrely in hosted mode
		// See:
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
		StringBuilder sql = new StringBuilder("SELECT ")
				.append((CharSequence) keywords)
				.append(" ")
				.append((CharSequence) columnList).append(" FROM ")
				.append((CharSequence) tableList);

		if (whereClause.length() > 0) {
			sql.append(" WHERE ").append((CharSequence) whereClause);
		}
		if (groupByClause.length() > 0) {
			sql.append(" GROUP BY ").append((CharSequence) groupByClause);
		}
		if (orderByClause.length() > 0) {
			sql.append(" ORDER BY ").append((CharSequence) orderByClause);
		}
		sql.append(" ").append(limitClause);

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
				if (errorHandler != null) {
					errorHandler.onFailure(e);
				}
			}
		});
	}

	public void execute(SqlTransaction tx, final SqlResultCallback callback) {
		tx.executeSql(sql(), parameters(), callback);
	}

	public class WhereClauseBuilder {
		String column;

		public WhereClauseBuilder() {
		}

		public WhereClauseBuilder(String column) {
			this.column = column;
		}

		public SqlQuery in(Collection<?> ids) {
			if (ids.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot match against empty list.");
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
			whereClause.append(" IN (").append(subquery.sql()).append(") ");

			parameters.addAll(subquery.parameters);

			return SqlQuery.this;
		}

		public SqlQuery equalTo(Object value) {
			whereClause.append(" = ? ");
			parameters.add(value);

			return SqlQuery.this;
		}
		
		public SqlQuery startsWith(String startingValue) {
			whereClause.append(" LIKE ?");
			parameters.add(startingValue + "%");
			return SqlQuery.this;
		}

		public SqlQuery isNull() {
			whereClause.append(" IS NULL ");
			return SqlQuery.this;
		}

		public SqlQuery like(String sqlParameter) {
			whereClause.append(" like ?");
			appendLikeParameter(sqlParameter);
			return SqlQuery.this;
		}

		public SqlQuery likeMany(List<String> sqlParameters) {
			assert (column != null); // Ensure you call onlyWhere first where
										// the column is set as a variable
			for (int i = 0; i < sqlParameters.size(); i++) {
				String parameter = sqlParameters.get(i);
				whereClause.append(" like ?");
				appendLikeParameter(parameter);
				if (i != sqlParameters.size() - 1) { // Append OR only to
														// non-last parameters
					or();
					onlyWhere(column);
				}
			}
			return SqlQuery.this;
		}

		public SqlQuery greaterThanOrEqualTo(Object value) {
			return comparison(">=", value);
		}

		public SqlQuery greaterThan(Object value) {
			return comparison(">", value);
		}

		public SqlQuery lessThan(Object value) {
			return comparison("<", value);
		}

		public SqlQuery lessThanOrEqualTo(Object value) {
			return comparison("<=", value);
		}

		private SqlQuery comparison(String operator, Object value) {
			whereClause.append(" ").append(operator).append(" ?");
			appendParameter(value);
			return SqlQuery.this;
		}
	}

	public class JoinBuilder {
		public SqlQuery on(String expr) {
			tableList.append(" ON (").append(expr).append(") ");
			return SqlQuery.this;
		}
	}

	@Override
  protected void doApply(SqlTransaction argument,
      final AsyncCallback<SqlResultSetRowList> callback) {
		execute(argument, new SqlResultCallback() {
			
			@Override
			public void onSuccess(SqlTransaction tx, SqlResultSet results) {
				callback.onSuccess(results.getRows());
			}
		});
  }

}
