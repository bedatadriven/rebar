package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.rebind.expr.primitives.PrimitiveFunExpr;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * An expression which evaluates to the result of a function call
 */
public class FunCallExpr extends ExprNode {

    private FunValExpr function;
    private List<ExprNode> arguments;

    public FunCallExpr(FunValExpr function, ExprNode... argument) {
        this.function = function;
        this.arguments = Lists.newArrayList(argument);
    }

    public FunValExpr getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return "(" + function + " " + Joiner.on(' ').join(arguments) + ")";
    }

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }

    public List<ExprNode> getArguments() {
        return arguments;
    }
}
