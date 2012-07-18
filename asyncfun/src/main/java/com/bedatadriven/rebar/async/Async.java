package com.bedatadriven.rebar.async;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Async {

	
	/**
	 * Creates a new {@code AsyncFunction} which applies {@code g} to 
	 * every element returned by {@code g}
	 * @param f
	 * @param g
	 * @return
	 */
	public static <Input1, IterableT extends Iterable<Element1>, Element1, Element2> 
	AsyncFunction<Input1, List<Element2>> map(
		final AsyncFunction<Input1, IterableT> f, 
		final Function<Element1, Element2> g) {
	
		return new AsyncFunction<Input1, List<Element2>>() {
	
			@Override
			protected void doApply(Input1 argument, final AsyncCallback<List<Element2>> callback) {
				f.apply(argument, new AsyncCallback<IterableT>() {
	
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
	
					@Override
					public void onSuccess(IterableT result) {
						try {
							List<Element2> list = Lists.newArrayList();
							
							for(Element1 element : result) {
								list.add(g.apply(element));
							}
							callback.onSuccess(list);
						} catch(Throwable caught) {
							callback.onFailure(caught);
						}
					}
				});
			}		
		};
	}

	/**
	 * Creates a new {@code AsyncFunction} which applies each of the given functions
	 * to its argument, in sequence.
	 * 
	 * @param functions
	 * @return
	 */
	public static <F> AsyncFunction<F, Void> sequence(AsyncFunction<F, ?>... functions) {
		return new AsyncSequence<F>(functions);
	}

	public static <Input1, IterableT extends Iterable<Element1>, Element1, Element2> 
	AsyncFunction<Input1, List<Element2>> map(
		final AsyncFunction<Input1, IterableT> f, 
		final AsyncFunction<Element1, Element2> g) {
	
		return new AsyncFunction<Input1, List<Element2>>() {
	
			@Override
			protected void doApply(Input1 argument, final AsyncCallback<List<Element2>> callback) {
				f.apply(argument, new AsyncCallback<IterableT>() {
	
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
	
					@Override
					public void onSuccess(IterableT result) {
						List<Element2> list = Lists.newArrayList();
						Iterator<Element1> iterator = result.iterator();
						
						applyNext(iterator, list);		
					}
					
					private void applyNext(
							final Iterator<Element1> iterator, 
							final List<Element2> list) 
					{
						if(!iterator.hasNext()) {
							callback.onSuccess(list);
						} else {
							g.apply(iterator.next(), new AsyncCallback<Element2>() {

								@Override
								public void onFailure(Throwable caught) {
									callback.onFailure(caught);
								}

								@Override
								public void onSuccess(Element2 result) {
									list.add(result);
									applyNext(iterator, list);
								}
							});
						}
					}
				});
			}		
		};
	}
	
	public static AsyncFunction<Void, Void> asFunction(final AsyncCommand command) {
		return new AsyncFunction<Void, Void>() {
			
			@Override
			protected void doApply(Void argument, AsyncCallback<Void> callback) {
				command.execute(callback);
			}
		};
	}
	
	public static <I extends Iterable<T>, T> Function<I, T> first() {
		return new Function<I, T>() {

			@Override
			public T apply(I input) {
				return input.iterator().next();
			}
		};
	}
	
}
