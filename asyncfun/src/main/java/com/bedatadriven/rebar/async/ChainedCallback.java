package com.bedatadriven.rebar.async;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ChainedCallback<T> implements AsyncCallback<T> {

  private final AsyncCallback<?> outer;

  public ChainedCallback(AsyncCallback<?> outer) {
    super();
    this.outer = outer;
  }

  @Override
  public final void onFailure(Throwable caught) {
    outer.onFailure(caught);
  }
}
