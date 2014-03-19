package com.bedatadriven.rebar.cell.rebind.diagnostic;

/**
 * Location within the source code
 */
public interface SourceLocation {

    public static final SourceLocation UNKNOWN = new SourceLocation() {

        @Override
        public String describeSourceLocation() {
            return "<unknown>";
        }
    };

    String describeSourceLocation();
}
