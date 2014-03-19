package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.Fun;
import com.bedatadriven.rebar.cell.rebind.module.CellFunction;

/**
 * An Expr node that evaluates to a function
 */
public class CellFunctionExpr extends FunValExpr {

    private final CellFunction function;

    public CellFunctionExpr(CellFunction function) {
        this.function = function;
    }

    public CellFunction getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
