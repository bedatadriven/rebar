package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.compiler.ast.Node;
import fr.umlv.tatoo.runtime.ast.Token;

/**
 * Identifies a location within the AST tree
 */
public class AstLocation implements SourceLinePosition {

    private final Node node;

    public AstLocation(Node node) {
        this.node = node;
    }

    private String tokens() {
        if(node.isToken()) {
            Token token = (Token)node;
            return token.getValue() + " [" + node.getKind() + "]";
        } else {
            return node.getKind().toString();
        }
    }
    @Override
    public String toString() {
        return describeSourceLocation();
    }

    @Override
    public String getQualifiedModuleName() {
        return node.getModuleAttribute();
    }

    @Override
    public int getLineNumber() {
        return node.getLineNumberAttribute();
    }

    @Override
    public int getColumnNumber() {
        return node.getColumnNumberAttribute();
    }

    @Override
    public String describeSourceLocation() {
        return node.getFileNameAttribute() + ", line " +
                node.getLineNumberAttribute() + " column " +
                node.getColumnNumberAttribute();
    }

    public Node getNode() {
        return node;
    }
}
