package com.bedatadriven.rebar.style.rebind.less;

import com.google.gwt.core.ext.TreeLogger;

/**
 * Provides the compiler with the root url and a
 * means of loading referenced style sheets
 */
public class LessCompilerContext {

  private final String sourceFileName;
  private TreeLogger logger;

  public LessCompilerContext(TreeLogger logger, String sourceFileName) {
    this.logger = logger;
    this.sourceFileName = sourceFileName;
  }

  public String getSourceFileName() {
    return sourceFileName;
  }

  public void info(String message) {
    logger.log(TreeLogger.Type.DEBUG, message);
  }

  public void warn(String message) {
    logger.log(TreeLogger.Type.WARN, message);
  }

}
