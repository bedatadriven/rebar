package com.bedatadriven.rebar.appcache.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ProgressEventHandler extends EventHandler {

  void onProgress(int filesComplete, int filesTotal);

}
