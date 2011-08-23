package com.bedatadriven.rebar.appcache.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateReadyEvent extends GwtEvent<UpdateReadyEventHandler> {

  /**
   * Handler type.
   */
  private static Type<UpdateReadyEventHandler> TYPE;


  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<UpdateReadyEventHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<UpdateReadyEventHandler>();
    }
    return TYPE;
  }


	@Override
  public final Type<UpdateReadyEventHandler> getAssociatedType() {
    return TYPE;
  }

 
  @Override
  public String toDebugString() {
    return super.toDebugString();
  }

	@Override
  protected void dispatch(UpdateReadyEventHandler handler) {
		handler.onAppCacheUpdateReady();
  }

}
