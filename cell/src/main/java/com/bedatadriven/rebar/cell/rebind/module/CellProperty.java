package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

/**
 * A value associated with an instance of a Cell
 */
public class CellProperty extends CellMember {

    private ExprNode initialValue;

    public CellProperty(String name, ExprNode value) {
        super(name);
        this.initialValue = value;
    }

    public ExprNode getInitialValue() {
        return initialValue;
    }
}
