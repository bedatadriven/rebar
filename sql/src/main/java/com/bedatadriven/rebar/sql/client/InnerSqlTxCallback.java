package com.bedatadriven.rebar.sql.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class InnerSqlTxCallback<T> extends SqlTransactionCallback {
	private AsyncCallback<T> outer;

	public InnerSqlTxCallback(AsyncCallback<T> outer) {
	  super();
	  this.outer = outer;
  }

	@Override
  public void onError(SqlException e) {
		outer.onFailure(e);
  }

	@Override
  public void onSuccess() {
	  outer.onSuccess(null);
  }
	
}
