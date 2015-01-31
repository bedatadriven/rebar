package com.bedatadriven.rebar.style.client;

/**
 * Marker interface for collections of icons.
 */
public interface IconSet {

  public boolean ensureInjected();

  public @interface Source {
    String value();

    int glyph() default 0;
  }
}
