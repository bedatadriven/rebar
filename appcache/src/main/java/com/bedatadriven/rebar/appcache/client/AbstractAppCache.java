package com.bedatadriven.rebar.appcache.client;

import com.bedatadriven.rebar.appcache.client.events.ProgressEvent;
import com.bedatadriven.rebar.appcache.client.events.ProgressEventHandler;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEvent;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractAppCache implements AppCache {

  private HandlerManager handlerManager;

  private HandlerManager getManager() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  @Override
  public HandlerRegistration addProgressHandler(ProgressEventHandler handler) {
    sinkProgressEvents();
    return getManager().addHandler(ProgressEvent.getType(), handler);
  }

  protected void sinkProgressEvents() {

  }

  protected final void fireProgress(int filesComplete, int filesTotal) {
    if (handlerManager != null) {
      handlerManager.fireEvent(new ProgressEvent(filesComplete, filesTotal));
    }
  }

  @Override
  public HandlerRegistration addUpdateReadyHandler(UpdateReadyEventHandler handler) {
    sinkUpdateReadyEvents();
    return getManager().addHandler(UpdateReadyEvent.getType(), handler);
  }

  protected void sinkUpdateReadyEvents() {

  }


  protected final void fireUpdateReady() {
    if (handlerManager != null) {
      handlerManager.fireEvent(new UpdateReadyEvent());
    }
  }
}
