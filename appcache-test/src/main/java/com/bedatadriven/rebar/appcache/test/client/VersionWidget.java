package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a version string loaded from the server
 */
public class VersionWidget implements IsWidget {

  private final LabeledValueWidget widget;

  public VersionWidget() {
    this.widget = new LabeledValueWidget(ElementId.VERSION_LABEL, "Loading...");
    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseURL() + "versionNumber.txt");
    requestBuilder.setCallback(new RequestCallback() {
      @Override
      public void onResponseReceived(Request request, Response response) {
        widget.setValue(response.getText());
      }

      @Override
      public void onError(Request request, Throwable exception) {
        widget.setValue("Error loading version.");
      }
    });
  }

  @Override
  public Widget asWidget() {
    return widget.asWidget();
  }
}
