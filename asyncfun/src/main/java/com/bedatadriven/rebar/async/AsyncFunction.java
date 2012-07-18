package com.bedatadriven.rebar.async;

import java.util.List;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;


public abstract class AsyncFunction<F,T> {

	public final void apply(F argument, AsyncCallback<T> callback) {
		try {
			doApply(argument, callback);
		} catch(Throwable caught) {
			callback.onFailure(caught);
		}
	}
	
	protected abstract void doApply(F argument, AsyncCallback<T> callback);

	public <T2> AsyncFunction<F,T2> compose(final AsyncFunction<T, T2> g) {
		return new AsyncFunction<F,T2>() {

			@Override
			protected void doApply(F argument, final AsyncCallback<T2> callback) {
				AsyncFunction.this.apply(argument, new ChainedCallback<T>(callback) {
					@Override
					public void onSuccess(T result) {
						g.apply(result, callback);
					}
				});
			}
		};
	}	
	
	@SuppressWarnings("unchecked")
	public <S, P> AsyncFunction<F, List<P>> map(final AsyncFunction<S, P> f) {
		AsyncFunction<F, Iterable<S>> self = (AsyncFunction<F, Iterable<S>>) this;
		return Async.map(self, f);
	}
	
	@SuppressWarnings("unchecked")
	public <S, P> AsyncFunction<F, List<P>> map(final Function<S, P> f) {
		AsyncFunction<F, Iterable<S>> self = (AsyncFunction<F, Iterable<S>>) this;
		return Async.map(self, f);
	}
	
	
	public <T2> AsyncFunction<F, T2> compose(final Function<T, T2> g) {
		return new AsyncFunction<F, T2>() {

			@Override
			protected void doApply(F argument, final AsyncCallback<T2> callback) {
				AsyncFunction.this.apply(argument, new ChainedCallback<T>(callback) {
					@Override
					public void onSuccess(T result) {
						try {
							callback.onSuccess(g.apply(result));
						} catch(Throwable caught) {
							callback.onFailure(caught);
						}
					}
				});
			}
		};
	}
	
	public AsyncFunction<F, T> composeSequence(AsyncFunction<T, ?>... functions) {
		final AsyncFunction<T, Void> sequence = new AsyncSequence<T>(functions);
		return new AsyncFunction<F, T>() {

			@Override
			protected void doApply(F argument, final AsyncCallback<T> callback) {
				AsyncFunction.this.apply(argument, new ChainedCallback<T>(callback) {
					@Override
					public void onSuccess(final T result) {
						sequence.apply(result, new ChainedCallback<Void>(callback) {
							@Override
							public void onSuccess(Void voidResult) {
								callback.onSuccess(result);
							}
						});
					}
				});
			}
		};
	}
	
	public AsyncFunction<F, Void> discardResult() {
		return compose(new Function<T, Void>() {

			@Override
			public Void apply(T input) {
				return null;
			}
		});
	}
}
