package com.bedatadriven.rebar.appcache.test.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;


public interface TestBundle extends ClientBundle {
  
  Style style();
  
  interface Style extends CssResource {

    String labeledValueWidget();
    
    String label();
    
  }
}
