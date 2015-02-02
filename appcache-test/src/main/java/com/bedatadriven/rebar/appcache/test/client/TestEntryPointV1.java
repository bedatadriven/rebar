package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point for "Version 1" of our test application
 */
public class TestEntryPointV1 implements EntryPoint {

  @Override
  public void onModuleLoad() {
    RootPanel.get().add(new TestPanel(AppVersion.VERSION1));
  }
}
