package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.rebind.gss.GssTree;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Constructs and models the Java Class that will implement our
 * Stylesheet interface
 */
public class StylesheetImpl {

    private GenerationParameters options;
    private AccessorBindings bindings;
    private GssTree tree;
    private String finalCss;

    public StylesheetImpl(GenerationParameters generationParameters, GssTree tree, AccessorBindings bindings) {
        this.options = generationParameters;
        this.bindings = bindings;
        this.tree = tree;
    }

    public void build(TreeLogger logger) throws UnableToCompleteException {
        tree.simplifyCSS();
        tree.stripVendorPrefixed(logger, options.getUserAgent());
        finalCss = tree.toCompactCSS();
    }

    public void write(StylesheetImplWriter writer) throws UnableToCompleteException {
        writer.writeGetName();
        writer.writeEnsureInjected();
        writer.writeClassNameMethods(bindings.getMap());
        writer.writeGetText(finalCss);
    }
}
