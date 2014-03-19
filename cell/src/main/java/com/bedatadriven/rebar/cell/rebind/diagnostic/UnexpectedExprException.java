package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.compiler.ast.Node;
import fr.umlv.tatoo.runtime.ast.Token;

/**
 * Exception thrown when the compiler encounters an unexpected or invalid expression
 */
public class UnexpectedExprException extends CompilerException {

    public UnexpectedExprException(Node node, String... helpMessage) {
        super(new AstLocation(node), "Unexpected expression " + formatNode(node), helpMessage);
    }

    public UnexpectedExprException(Node node) {
        super(new AstLocation(node), "Unexpected expression " + formatNode(node));
    }

    private static String formatNode(Node node) {
        if(node instanceof Token) {
            return ((Token) node).getValue() + " (" + node.getClass().getSimpleName() + ")";
        } else {
            return node.getKind().toString();
        }

    }
}
