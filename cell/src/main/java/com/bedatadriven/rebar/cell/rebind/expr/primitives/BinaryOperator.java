package com.bedatadriven.rebar.cell.rebind.expr.primitives;

import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

import java.util.List;

/**
 * Binary Operators accept two arguments of the same type,
 * so do any necessary conversions here
 */
public abstract class BinaryOperator extends PrimitiveFunExpr {

    @Override
    public final AtomicExpr apply(List<ExprNode> arguments) {
        if(arguments.size() != 2) {
            throw new PrimitiveException("Expected two arguments for " + getClass().getName());
        }
        ExprNode x = arguments.get(0);
        ExprNode y = arguments.get(1);

        if(!(x instanceof AtomicExpr) || !(y instanceof AtomicExpr)) {
            throw new PrimitiveException("Expected two constant values as arguments, got: " +
                  x + ", " + y);
        }

        String sx = ((AtomicExpr)x).asString();
        String sy = ((AtomicExpr)y).asString();

        return apply(sx, sy);
    }

    protected abstract AtomicExpr apply(String x, String y);
}
