package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * Internationalized constants
 */
public interface Hello extends Constants {
  
  public static final Hello INSTANCE = GWT.create(Hello.class);

  @DefaultStringValue("Hello world!")
  String helloWorld();

}
