package com.bedatadriven.rebar.sql.client.fn;

import com.bedatadriven.rebar.async.ChainedCallback;
import com.bedatadriven.rebar.sql.client.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Iterator;
import java.util.List;

public abstract class QueryFunction<T> extends TxAsyncFunction<T, SqlResultSetRowList> {


  public static QueryFunction<Void> query(final String sql) {
    return new QueryFunction<Void>() {

      @Override
      protected void query(SqlTransaction tx, Void argument, SqlResultCallback callback) {
        tx.executeSql(sql, callback);
      }
    };
  }

  @Override
  protected final void doApply(SqlTransaction tx, T argument,
                               final AsyncCallback<SqlResultSetRowList> callback) {
    query(tx, argument, new SqlResultCallback() {

      @Override
      public void onSuccess(SqlTransaction tx, SqlResultSet results) {
        callback.onSuccess(results.getRows());
      }
    });
  }

  protected abstract void query(SqlTransaction tx, T argument, SqlResultCallback callback);

  public TxAsyncFunction<T, SqlResultSetRow> first() {
    return new TxAsyncFunction<T, SqlResultSetRow>() {

      @Override
      protected void doApply(SqlTransaction tx, T argument,
                             final AsyncCallback<SqlResultSetRow> callback) {
        QueryFunction.this.apply(tx, argument, new ChainedCallback<SqlResultSetRowList>(callback) {

          @Override
          public void onSuccess(SqlResultSetRowList result) {
            callback.onSuccess(result.get(0));
          }
        });
      }
    };
  }

  /**
   * Applies the given function to all the resulting rows, in sequence.
   *
   * @param g an asynchronous function
   */
  public <S> TxAsyncFunction<T, List<S>> mapSequentially(final TxAsyncFunction<SqlResultSetRow, S> g) {
    return new TxAsyncFunction<T, List<S>>() {

      @Override
      protected void doApply(final SqlTransaction tx, T argument,
                             final AsyncCallback<List<S>> callback) {
        QueryFunction.this.apply(tx, argument, new ChainedCallback<SqlResultSetRowList>(callback) {

          @Override
          public void onSuccess(SqlResultSetRowList rows) {
            List<S> resultList = Lists.newArrayList();
            applyNext(tx, rows.iterator(), resultList, callback);
          }
        });
      }

      private void applyNext(final SqlTransaction tx,
                             final Iterator<SqlResultSetRow> rowIt,
                             final List<S> resultList,
                             final AsyncCallback<List<S>> callback) {
        if (!rowIt.hasNext()) {
          callback.onSuccess(resultList);
        } else {
          g.apply(tx, rowIt.next(), new ChainedCallback<S>(callback) {

            @Override
            public void onSuccess(S result) {
              resultList.add(result);
              applyNext(tx, rowIt, resultList, callback);
            }
          });
        }
      }
    };
  }

  public <S> TxAsyncFunction<T, List<S>> mapSequentially(
      Function<SqlResultSetRow, S> g) {

    return mapSequentially(AsyncSql.valueOf(g));
  }

  public <S> TxAsyncFunction<T, List<S>> parallelMap(final TxAsyncFunction<SqlResultSetRow, S> g) {
    return new TxAsyncFunction<T, List<S>>() {

      @Override
      protected void doApply(final SqlTransaction tx, T argument,
                             final AsyncCallback<List<S>> callback) {
        QueryFunction.this.apply(tx, argument, new ChainedCallback<SqlResultSetRowList>(callback) {

          @Override
          public void onSuccess(SqlResultSetRowList rows) {
            final List<S> results = Lists.newArrayList();

            if (rows.size() == 0) {
              callback.onSuccess(results);
            } else {
              final boolean[] completed = new boolean[rows.size()];

              for (int i = 0; i != rows.size(); ++i) {
                final int rowIndex = i;
                results.add(null);
                g.apply(tx, rows.get(i), new AsyncCallback<S>() {

                  @Override
                  public void onSuccess(S result) {
                    results.set(rowIndex, result);
                    completed[rowIndex] = true;

                    // check if this is the last one
                    for (boolean c : completed) {
                      if (!c) {
                        return;
                      }
                    }

                    //if so, return
                    callback.onSuccess(results);
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                  }
                });
              }
            }
          }
        });
      }

    };
  }

}
