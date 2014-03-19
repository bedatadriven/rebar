package com.bedatadriven.rebar.cell.rebind.diagnostic;


import com.bedatadriven.rebar.cell.compiler.ast.IdToken;
import com.bedatadriven.rebar.cell.compiler.ast.Node;
import com.bedatadriven.rebar.cell.rebind.expr.Scope;

public class UnresolvedSymbolException extends CompilerException {

    public UnresolvedSymbolException(Scope scope, IdToken symbol) {
        super(new AstLocation(symbol), "Could not resolve " + symbol);
    }
}
