package com.bedatadriven.rebar.cell.rebind.expr;

/**
 * An expression whose value depends on a condition
 */
public class ConditionalExpr extends ExprNode {

    private ExprNode condition;
    private ExprNode ifTrue;
    private ExprNode ifFalse;

    public ConditionalExpr(ExprNode condition, ExprNode ifTrue, ExprNode ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public ExprNode getCondition() {
        return condition;
    }

    public ExprNode getIfTrue() {
        return ifTrue;
    }

    public ExprNode getIfFalse() {
        return ifFalse;
    }

    @Override
    public String toString() {
        return "(" + condition + " ? " + ifTrue + " : " + ifFalse + ")";
    }

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitConditionalExpr(this);
    }
}
