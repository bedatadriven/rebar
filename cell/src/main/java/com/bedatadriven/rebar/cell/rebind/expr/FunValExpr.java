package com.bedatadriven.rebar.cell.rebind.expr;


public abstract class FunValExpr extends ExprNode {

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
       return visitor.visitFunValue(this);
    }
}
