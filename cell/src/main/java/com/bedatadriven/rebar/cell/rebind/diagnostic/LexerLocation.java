package com.bedatadriven.rebar.cell.rebind.diagnostic;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;


public class LexerLocation implements SourceLinePosition {

    private final int columnNumber;
    private final int lineNumber;
    private String qualifiedModuleName;

    public LexerLocation(String qualifiedModuleName, LocationTracker tracker) {
        this.qualifiedModuleName = qualifiedModuleName;
        this.lineNumber = tracker.getLineNumber();
        this.columnNumber = tracker.getColumnNumber();
    }

    @Override
    public String getQualifiedModuleName() {
        return qualifiedModuleName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public String toString() {
        return describeSourceLocation();
    }

    @Override
    public String describeSourceLocation() {
        return qualifiedModuleName + ", line " + lineNumber + ", column " + columnNumber;
    }
}
