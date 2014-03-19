package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.rebind.diagnostic.UnexpectedExprException;
import com.bedatadriven.rebar.cell.rebind.module.AbstractVisitor;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Parses the attributes in an inline tag
 */
public class AttrParser extends AbstractVisitor {

    private final Scope scope;
    private final Map<String, ExprNode> attributes = Maps.newHashMap();

    public AttrParser(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Void visit(AttrsDollarAccess attr, Void _param) throws RuntimeException {
        IdToken name = attr.getId();
        ExprNode value = attr.getDollarAccess().accept(new ExprBuilder(), scope);
        attributes.put(name.getValue(), value);
        return null;
    }

    @Override
    public Void visit(AttrsStringLiteral attr, Void _param) throws RuntimeException {
        IdToken name = attr.getId();
        ExprNode value = new AtomicExpr(attr.getStringLiteral().getValue());
        attributes.put(name.getValue(), value);
        return null;
    }

    @Override
    public Void visit(AttrsEmpty attr, Void _param) throws RuntimeException {
        return descend(attr);
    }

    @Override
    protected Void visit(Attrs attrs, Void _param) throws RuntimeException {
        return super.visit(attrs, _param);
    }

    public Map<String, ExprNode> getAttributes() {
        return attributes;
    }

    @Override
    protected Void visit(Node node, Void _param) {
        throw new UnexpectedExprException(node);
    }
}
