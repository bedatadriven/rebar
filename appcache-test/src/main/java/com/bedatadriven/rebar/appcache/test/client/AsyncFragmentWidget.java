package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class AsyncFragmentWidget implements IsWidget {
  
  private Button button;

  public AsyncFragmentWidget() {
    button = new Button("Load Async Fragment");
    button.getElement().setId(ElementId.LOAD_ASYNC_BUTTON.id());
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        GWT.runAsync(new RunAsyncCallback() {
          @Override
          public void onFailure(Throwable reason) {
            button.setText("Load failed.");  
          }

          @Override
          public void onSuccess() {
            button.setText(Messages.ASYNC_FRAGMENT_LOADED);
          }
        });
      }
    });
  }

  @Override
  public Widget asWidget() {
    return button;
  }
}
