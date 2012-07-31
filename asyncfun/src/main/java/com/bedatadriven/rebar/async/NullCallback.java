

package com.bedatadriven.rebar.async;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NullCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable throwable) { /* NO OP */ }

	@Override
	public void onSuccess(T t) { /* NO OP */ }

	public static <T> NullCallback<T> create() {
		return new NullCallback<T>();
	}

	public static NullCallback<Void> forVoid() {
		return new NullCallback<Void>();
	}
}
