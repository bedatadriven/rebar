package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.rebind.module.ElementType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 3/19/14.
 */
public class TagExpr extends ExprNode {

    private ElementType elementType;
    private Map<String, ExprNode> fields;
    private List<ExprNode> children;

    public TagExpr(ElementType elementType) {
        this.elementType = elementType;
        this.fields = Maps.newHashMap();
        this.children = Lists.newArrayList();
    }

    public TagExpr(ElementType elementType, Map<String, ExprNode> fields, List<ExprNode> children) {
        this.elementType = elementType;
        this.fields = fields;
        this.children = children;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitTag(this);
    }

    public boolean isAttributeDefined(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public ElementType getElementType() {
        return elementType;
    }

    public Iterable<? extends String> getAttributeNames() {
        return fields.keySet();
    }

    public ExprNode getAttribute(String attr) {
        return fields.get(attr);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public List<ExprNode> getChildren() {
        return children;
    }
}
