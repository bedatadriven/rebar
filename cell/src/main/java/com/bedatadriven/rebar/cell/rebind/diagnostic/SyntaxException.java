package com.bedatadriven.rebar.cell.rebind.diagnostic;

import fr.umlv.tatoo.runtime.lexer.LexingException;

/**
 * Wraps errors from the lexer/parser
 */
public class SyntaxException extends CompilerException {

    public SyntaxException(SourceLocation location, Exception e) {
        super(location, "Syntax error", e);
    }
}
