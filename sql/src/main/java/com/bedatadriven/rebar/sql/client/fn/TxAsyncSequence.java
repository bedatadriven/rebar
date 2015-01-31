package com.bedatadriven.rebar.sql.client.fn;

import com.bedatadriven.rebar.async.ChainedCallback;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Iterator;
import java.util.List;

public class TxAsyncSequence<F, Void> extends TxAsyncFunction<F, Void> {


  private List<TxAsyncFunction<F, ?>> functions;

  public TxAsyncSequence(TxAsyncFunction<F, ?>... functions) {
    this.functions = Lists.newArrayList(functions);
  }

  @Override
  protected void doApply(SqlTransaction tx, F argument, AsyncCallback<Void> callback) {
    Iterator<TxAsyncFunction<F, ?>> iterator = functions.iterator();
    applyNext(tx, argument, iterator, callback);
  }

  private void applyNext(final SqlTransaction tx, final F argument, final Iterator<TxAsyncFunction<F, ?>> iterator,
                         final AsyncCallback<Void> callback) {
    if (!iterator.hasNext()) {
      callback.onSuccess(null);
    } else {
      TxAsyncFunction<F, ?> f = iterator.next();
      f.apply(tx, argument, new ChainedCallback(callback) {
        @Override
        public void onSuccess(Object result) {
          applyNext(tx, argument, iterator, callback);
        }
      });
    }
  }
}
