package com.bedatadriven.rebar.style.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class TestPanel implements IsWidget {

  private static TestPanelUiBinder ourUiBinder = GWT.create(TestPanelUiBinder.class);
  private final HTMLPanel rootElement;

  public TestPanel() {
    rootElement = ourUiBinder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return rootElement;
  }

  interface TestPanelUiBinder extends UiBinder<HTMLPanel, TestPanel> {
  }
}