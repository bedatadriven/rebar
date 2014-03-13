package com.bedatadriven.rebar.style.rebind.css;

import com.google.gwt.core.ext.TreeLogger;

/**
 * Provides the compiler with the root url and a
 * means of loading referenced style sheets
 */
public class LessCompilerContext {

    private TreeLogger logger;
    private final String sourceFileName;

    public LessCompilerContext(TreeLogger logger, String sourceFileName) {
        this.logger = logger;
        this.sourceFileName = sourceFileName;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void info(String message) {
        logger.log(TreeLogger.Type.INFO, message);
    }

    public void warn(String message) {
        logger.log(TreeLogger.Type.WARN, message);
    }

}
