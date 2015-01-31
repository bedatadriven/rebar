package com.bedatadriven.rebar.style.client;


import com.google.gwt.core.client.GWT;

public interface TestIconSet extends IconSet {

  public static final TestIconSet INSTANCE = GWT.create(TestIconSet.class);

  @Source(value = "fontawesome.svg", glyph = 0xf115)
  String folder();

  @Source("heart.svg")
  String heart();


}
