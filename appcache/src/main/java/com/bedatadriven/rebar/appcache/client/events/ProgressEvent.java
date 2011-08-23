package com.bedatadriven.rebar.appcache.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ProgressEvent extends GwtEvent<ProgressEventHandler>{

  /**
   * Handler type.
   */
  private static Type<ProgressEventHandler> TYPE;


  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ProgressEventHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ProgressEventHandler>();
    }
    return TYPE;
  }

  private int filesComplete;
  private int filesTotal;

  
  public ProgressEvent(int filesComplete, int filesTotal) {
	  super();
	  this.filesComplete = filesComplete;
	  this.filesTotal = filesTotal;
  }

	public int getFilesComplete() {
  	return filesComplete;
  }


	public int getFilesTotal() {
  	return filesTotal;
  }



	@Override
  public final Type<ProgressEventHandler> getAssociatedType() {
    return TYPE;
  }

 
  @Override
  public String toDebugString() {
    return super.toDebugString() + filesComplete + "/" + filesTotal;
  }

	@Override
  protected void dispatch(ProgressEventHandler handler) {
		handler.onProgress(filesComplete, filesTotal);
  }

}
