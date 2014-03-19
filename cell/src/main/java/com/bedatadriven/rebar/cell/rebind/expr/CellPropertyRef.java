package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.rebind.module.CellProperty;

/**
 * An expression that evaluates to a cell's property values
 */
public class CellPropertyRef extends ExprNode {

    private String name;
    private CellProperty property;

    public CellPropertyRef(String name, CellProperty property) {
        this.name = name;
        this.property = property;
    }

    @Override
    public String toString() {
        return "this." + name;
    }

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitCellPropertyRef(this);
    }

    public String getName() {
        return name;
    }

    public CellProperty getProperty() {
        return property;
    }
}
