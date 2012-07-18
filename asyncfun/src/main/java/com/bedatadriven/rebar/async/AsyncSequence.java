package com.bedatadriven.rebar.async;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;

class AsyncSequence<F> extends AsyncFunction<F, Void> {

	private List<AsyncFunction<F, ?>> functions;
	
	public AsyncSequence(AsyncFunction<F, ?>... functions) {
		this.functions = Lists.newArrayList(functions);
	}
	
	@Override
	protected void doApply(F argument, AsyncCallback<Void> callback) {
		Iterator<AsyncFunction<F, ?>> iterator = functions.iterator();
		applyNext(argument, iterator, callback);
	}

	private void applyNext(final F argument, final Iterator<AsyncFunction<F, ?>> iterator,
			final AsyncCallback<Void> callback) {
		if(!iterator.hasNext()) {
			callback.onSuccess(null);
		} else {
			AsyncFunction<F, ?> f = iterator.next();
			f.apply(argument, new ChainedCallback(callback) {
				@Override
				public void onSuccess(Object result) {
					applyNext(argument, iterator, callback);
				}
			});
		}
	}	
}
