package com.bedatadriven.rebar.bootstrapTest.client;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface HomeClientBundle extends ClientBundle {

	public static final HomeClientBundle INSTANCE = GWT.create(HomeClientBundle.class);
	
	@Source("home.less")
	MyStyle style();
	
	interface MyStyle extends Bootstrap {
		
	}
	
}
