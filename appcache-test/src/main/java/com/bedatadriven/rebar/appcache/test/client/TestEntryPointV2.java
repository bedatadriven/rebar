package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class TestEntryPointV2 implements EntryPoint {
  @Override
  public void onModuleLoad() {
    RootPanel.get().add(new TestPanel(AppVersion.VERSION2));
  }
}
