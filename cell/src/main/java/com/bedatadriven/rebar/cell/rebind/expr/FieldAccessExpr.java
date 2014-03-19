package com.bedatadriven.rebar.cell.rebind.expr;

/**
 * Expression that evaluates to the value of a field {@code obj.field}
 */
public class FieldAccessExpr extends ExprNode {

    private ExprNode value;
    private String fieldName;

    public FieldAccessExpr(ExprNode value, String fieldName) {
        this.value = value;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ExprNode getValue() {
        return value;
    }

    public void setValue(ExprNode value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitFieldAccess(this);
    }
}
