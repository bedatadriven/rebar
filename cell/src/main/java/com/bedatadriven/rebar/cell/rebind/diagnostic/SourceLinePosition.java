package com.bedatadriven.rebar.cell.rebind.diagnostic;

/**
 * Provides some context as to the location of the error in the source.
 */
public interface SourceLinePosition extends SourceLocation {

    String getQualifiedModuleName();

    int getLineNumber();

    int getColumnNumber();
}
