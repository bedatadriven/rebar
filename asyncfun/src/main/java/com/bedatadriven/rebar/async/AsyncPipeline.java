package com.bedatadriven.rebar.async;


import com.google.gwt.user.client.rpc.AsyncCallback;

public class AsyncPipeline {

	private final AsyncCommand[] commands;
	private AsyncCallback<Void> finalCallback;
	private int commandIndex = 0;
	private boolean running;
	
	public AsyncPipeline(AsyncCommand... commands) {
		this.commands = commands;
	}

	public void start(AsyncCallback<Void> callback) {
		if(!running) {
			running = true;
			finalCallback = callback;
			commandIndex = 0;
			executeNext();
		}
	}
	
	public void start() {
		start(new NullCallback<Void>());
	}
	
	private void executeNext() {
		this.running = true;
		if(commandIndex < commands.length) {
			int nextCommand = commandIndex++;
			try {
				commands[nextCommand].execute(new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						AsyncPipeline.this.executeNext();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						running = false;
						finalCallback.onFailure(caught);
					}
				});
			} catch(Exception e) {
				running = false;
				finalCallback.onFailure(e);
			}
		} else {
			running = false;
			finalCallback.onSuccess(null);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public static void execute(AsyncCallback<Void> callback, AsyncCommand... commands) {
		new AsyncPipeline(commands).start(callback);
	}
	
	public static void execute(AsyncCommand... commands) {
		execute(new NullCallback<Void>(), commands);
	}
}
