package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.rebind.module.Cell;
import com.bedatadriven.rebar.cell.rebind.module.ElementType;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Evaluates to a tag, either a dom element or a composite
 */
public class TagConstructorExpr extends ExprNode {

    private ElementType type;
    private Map<String, ExprNode> fields;
    private List<ExprNode> children;

    public TagConstructorExpr(Cell type) {
        this.type = type;
        this.fields = Maps.newHashMapWithExpectedSize(0);
    }

    public TagConstructorExpr(ElementType type, Map<String, ExprNode> fields, List<ExprNode> children) {
        this.type = type;
        this.fields = fields;
        this.children = children;
    }

    public ElementType getElementType() {
        return type;
    }

    public Collection<String> getAttributeNames() {
        return fields.keySet();
    }

    public ExprNode getAttribute(String attr) {
        return fields.get(attr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(type.getSimpleName());
        for(Map.Entry<String, ExprNode> field : fields.entrySet()) {
            sb.append(" ").append(field.getKey()).append("=")
                    .append("(").append(field.getValue()).append(")");
        }
        sb.append("/>");
        return sb.toString();
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitConstructor(this);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public List<ExprNode> getChildren() {
        return children;
    }
}
