package com.bedatadriven.rebar.cell.rebind.expr;

/**
 * An expression which references a definition made
 * inside the function
 */
public class LocalDefRefExpr extends ExprNode {
    private String name;
    private ExprNode value;

    public LocalDefRefExpr(String name, ExprNode value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ExprNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitLocalReference(this);
    }
}
