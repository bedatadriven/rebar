package com.bedatadriven.rebar.cell.rebind.eval;

import com.bedatadriven.rebar.cell.rebind.expr.CellPropertyRef;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;
import com.bedatadriven.rebar.cell.rebind.expr.TagExpr;
import com.bedatadriven.rebar.cell.rebind.module.Cell;


public class EvalScope {

    private final Cell cell;
    private TagExpr currentTag;

    public EvalScope(Cell cell, TagExpr currentTag) {
        this.cell = cell;
        this.currentTag = currentTag;
    }

    public ExprNode resolveCellProperty(CellPropertyRef ref) {
        if(currentTag.isAttributeDefined(ref.getName())) {
            return currentTag.getAttribute(ref.getName());
        }
        return ref.getProperty().getInitialValue();
    }
}
