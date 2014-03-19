package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.Expr;
import com.bedatadriven.rebar.cell.rebind.diagnostic.SourceLocation;
import com.bedatadriven.rebar.cell.rebind.module.CellProperty;

import java.util.Map;

/**
 * An expression which represents a property mutation
 */
public class MutatorExpr extends ExprNode {

    private Map<String, ExprNode> updatedValues;

    public MutatorExpr(Map<String, ExprNode> updatedValues) {
        this.updatedValues = updatedValues;
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitMutation(this);
    }

}
