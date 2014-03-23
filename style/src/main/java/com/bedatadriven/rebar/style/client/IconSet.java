package com.bedatadriven.rebar.style.client;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Marker interface for collections of icons.
 *
 *
 */
public interface IconSet {

    public @interface Source {
        String value();
        int glyph() default 0;
    }

    public boolean ensureInjected();
}
