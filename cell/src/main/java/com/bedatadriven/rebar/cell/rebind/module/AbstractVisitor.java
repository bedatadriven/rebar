package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.LcurlToken;
import com.bedatadriven.rebar.cell.compiler.ast.Node;
import com.bedatadriven.rebar.cell.compiler.ast.RcurlToken;
import com.bedatadriven.rebar.cell.compiler.ast.Visitor;


public abstract class AbstractVisitor extends Visitor<Void, Void, RuntimeException> {

    protected final Void descend(Node script_member) {
        for(Node node : script_member.nodeList()) {
            node.accept(this, null);
        }
        return null;
    }

    @Override
    public final Void visit(LcurlToken lcurl, Void _param) throws RuntimeException {
        return descend(lcurl);
    }

    @Override
    public final Void visit(RcurlToken rcurl, Void _param) throws RuntimeException {
        return descend(rcurl);
    }

    @Override
    protected abstract Void visit(Node node, Void _param);
}
