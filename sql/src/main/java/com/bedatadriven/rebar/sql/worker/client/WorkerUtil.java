package com.bedatadriven.rebar.sql.worker.client;

import com.google.gwt.gears.client.workerpool.WorkerPool;

public class WorkerUtil {

	
	public static native void sendMessageSafe(WorkerPool pool, String message, int ownerWorkerId) /*-{
  	pool.sendMessage(String(message), Number(ownerWorkerId));
	}-*/;

}
