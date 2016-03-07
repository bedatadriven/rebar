package com.bedatadriven.rebar.appcache.test.client;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.bedatadriven.rebar.appcache.client.events.UpdateReadyEventHandler;
import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Verifies that we can load a network resource when the appcache is active
 */
public class NetworkResourceWidget implements IsWidget {

  private final LabeledValueWidget widget;

  public NetworkResourceWidget() {
    widget = new LabeledValueWidget(ElementId.NETWORK_RESOURCE, "Not loaded");
    widget.addButton(ElementId.FETCH_NETWORK_RESOURCE, new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        RequestBuilder req = new RequestBuilder(RequestBuilder.GET, "/network-resource.txt");
        req.setCallback(new RequestCallback() {
          @Override
          public void onResponseReceived(Request request, Response response) {
            String text = response.getText();
            if(text == null || text.length() == 0) {
              widget.setValue("Status: " + response.getStatusCode());
            } else {
              widget.setValue(text);
            }
          }

          @Override
          public void onError(Request request, Throwable exception) {
            widget.setValue("ERROR: " + exception.getMessage());
          }
        });
        try {
          req.send();
        } catch (RequestException e) {
          widget.setValue("RequestException: " + e.getMessage());
        }
      }
    });
  }

  @Override
  public Widget asWidget() {
    return widget.asWidget();
  }
}
