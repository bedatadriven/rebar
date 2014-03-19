package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.compiler.ast.Node;

/**
 * Created by alex on 3/18/14.
 */
public class NotImplementedException extends CompilerException {

    public NotImplementedException(Node node) {
        super(new AstLocation(node), "Not Implemented");
    }
}
