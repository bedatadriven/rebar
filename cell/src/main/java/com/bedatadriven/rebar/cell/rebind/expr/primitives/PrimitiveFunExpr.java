package com.bedatadriven.rebar.cell.rebind.expr.primitives;

import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;
import com.bedatadriven.rebar.cell.rebind.expr.FunValExpr;

import java.util.List;

/**
 * Superclass of
 */
public abstract class PrimitiveFunExpr extends FunValExpr {

    public abstract AtomicExpr apply(List<ExprNode> arguments);

    public String getName() {
        return getClass().getSimpleName();
    }
}
