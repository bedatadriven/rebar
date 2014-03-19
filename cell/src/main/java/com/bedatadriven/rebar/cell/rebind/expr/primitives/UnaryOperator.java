package com.bedatadriven.rebar.cell.rebind.expr.primitives;

import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

import java.util.List;

/**
 *
 */
public class UnaryOperator extends PrimitiveFunExpr {

    @Override
    public AtomicExpr apply(List<ExprNode> arguments) {
        if(arguments.size() != 1) {
            throw new IllegalArgumentException();
        }

        throw new UnsupportedOperationException();
    }
}
