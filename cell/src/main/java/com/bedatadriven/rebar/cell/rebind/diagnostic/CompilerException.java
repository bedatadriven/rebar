package com.bedatadriven.rebar.cell.rebind.diagnostic;

import java.util.List;

public class CompilerException extends RuntimeException {

    private SourceLocation location;
    private String[] helpText;
    private List<NamedSourceLocation> namedSourceLocations;

    protected CompilerException(SourceLocation location, String message) {
        super(message);
        this.location = location;
    }

    public CompilerException(SourceLocation location, String message, String... helpText) {
        super(message);
        this.location = location;
        this.helpText = helpText;
    }

    public CompilerException(SourceLocation location, String message, Exception e) {
        super(message, e);
        this.location = location;
    }

    public static CompilerException wrap(SourceLocation location, Exception e) {
        if(e instanceof CompilerException) {
            return new CompilerException(location, e.getMessage(), e);
        } else {
            return new CompilerException(location, "unexpected exception", e);
        }
    }


    public CompilerException addNamedSourceLocation(String name, SourceLocation sourceLocation) {
        namedSourceLocations.add(new NamedSourceLocation(name, sourceLocation));
        return this;
    }

    public String[] getHelpText() {
        return helpText;
    }

    public SourceLocation getLocation() {
        return location;
    }

}
