package com.bedatadriven.rebar.cell.rebind.expr.primitives;

import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

/**
 * Plus operator
 */
public class PlusOperator extends BinaryOperator {
    @Override
    protected AtomicExpr apply(String x, String y) {
        return new AtomicExpr(x + y);
    }

    @Override
    public String toString() {
        return "+";
    }
}
