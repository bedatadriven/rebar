package com.bedatadriven.rebar.cell.rebind.diagnostic;

/**
 * References another source location by name, such as the location
 * of the previous definition.
 */
public class NamedSourceLocation {
    private String name;
    private SourceLocation location;

    public NamedSourceLocation(String name, SourceLocation location) {
        this.name = name;
        this.location = location;
    }
}
