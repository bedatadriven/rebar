package com.bedatadriven.rebar.sql.client.fn;

import com.bedatadriven.rebar.async.ChainedCallback;
import com.bedatadriven.rebar.async.NullCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Transactional, asynchronous function
 *
 * @param <F> type of this function's argument
 * @param <T> this function's result type
 */
public abstract class TxAsyncFunction<F, T> {
  public final void apply(SqlTransaction tx, F argument, AsyncCallback<T> callback) {
    try {
      doApply(tx, argument, callback);
    } catch (Throwable caught) {
      callback.onFailure(caught);
    }
  }

  public final void apply(SqlTransaction tx, F argument) {
    apply(tx, argument, new NullCallback<T>());
  }

  protected abstract void doApply(SqlTransaction tx, F argument, AsyncCallback<T> callback);

  public <T2> TxAsyncFunction<F, T2> compose(final TxAsyncFunction<T, T2> g) {
    return new TxAsyncFunction<F, T2>() {

      @Override
      protected void doApply(final SqlTransaction tx, F argument, final AsyncCallback<T2> callback) {
        TxAsyncFunction.this.apply(tx, argument, new ChainedCallback<T>(callback) {
          @Override
          public void onSuccess(T result) {
            g.apply(tx, result, callback);
          }
        });
      }
    };
  }

  public <T2> TxAsyncFunction<F, T2> compose(final Function<T, T2> g) {
    return new TxAsyncFunction<F, T2>() {

      @Override
      protected void doApply(SqlTransaction tx, F argument, final AsyncCallback<T2> callback) {
        TxAsyncFunction.this.apply(tx, argument, new ChainedCallback<T>(callback) {
          @Override
          public void onSuccess(T result) {
            try {
              callback.onSuccess(g.apply(result));
            } catch (Throwable caught) {
              callback.onFailure(caught);
            }
          }
        });
      }
    };
  }

  public TxAsyncFunction<F, Void> discardResult() {
    return compose(new Function<T, Void>() {

      @Override
      public Void apply(T input) {
        return null;
      }
    });
  }
}
