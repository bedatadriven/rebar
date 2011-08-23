package com.bedatadriven.rebar.appcache.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface UpdateReadyEventHandler extends EventHandler {
	
	void onAppCacheUpdateReady();
	
}
