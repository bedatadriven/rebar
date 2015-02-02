package com.bedatadriven.rebar.appcache.test.client;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEventHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Allows the user to switch to the application after a new 
 * version of the application have been loaded in the background.
 */
public class UpdateAppWidget implements IsWidget {

  private final LabeledValueWidget widget;

  public UpdateAppWidget(final AppCache appCache) {
    widget = new LabeledValueWidget(ElementId.UPDATE_STATUS, Messages.APP_IS_UP_TO_DATE);
    widget.addButton(ElementId.CHECK_FOR_UPDATE, new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        appCache.checkForUpdate();
      }
    });
    final Button updateButton = widget.addButton(ElementId.LOAD_UPDATE, new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.Location.reload();
      }
    });
    updateButton.setEnabled(false);

    appCache.addUpdateReadyHandler(new UpdateReadyEventHandler() {
      @Override
      public void onAppCacheUpdateReady() {
        widget.setValue(Messages.UPDATE_AVAILABLE);
        updateButton.setEnabled(true);
      }
    });
    
  }

  @Override
  public Widget asWidget() {
    return widget.asWidget();
  }
}
