package com.bedatadriven.rebar.bootstrapTest.client;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Home extends Composite {

	private static HomeUiBinder uiBinder = GWT.create(HomeUiBinder.class);

	@UiField Bootstrap b;
	
	interface HomeUiBinder extends UiBinder<Widget, Home> {
	}

	public Home() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
