package com.bedatadriven.rebar.style.rebind.icons;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Composes the CSS rule for an icon using the best strategy for the given browser
 */
public interface IconStrategy {

    void appendCommonDeclarations(StringBuilder css);

    void appendDeclarations(TreeLogger logger, IconSource source, StringBuilder css) throws UnableToCompleteException;
}
