package com.bedatadriven.rebar.bootstrapTest.client;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class BootstrapTest implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		Bootstrap.INSTANCE.ensureInjected();
		
		Home home = new Home();
		RootPanel.get().add(home);
		
	}	
}
