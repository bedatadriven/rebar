package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.ExprFun;
import com.bedatadriven.rebar.cell.rebind.diagnostic.SourceLocation;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;
import com.bedatadriven.rebar.cell.rebind.expr.FunValueBuilder;

/**
 * A function defined as part of a Cell. All Cell Functions must be pure:
 * no side effects.
 */
public class CellFunction extends CellMember implements SourceLocation {

    private Cell cell;
    private ExprNode body;


    public CellFunction(Cell cell, String name, ExprFun fun) {
        super(name);
        this.cell = cell;
        this.body = new FunValueBuilder(this).build(fun.getBlock());
    }

    public Cell getCell() {
        return cell;
    }

    public  ExprNode getBody() {
        return body;
    }

    public String getQualfiedName() {
        return cell.getQualifiedName() + getName();
    }

    public String toString() {
        return cell.getSimpleName() + "." + getName() + "()";
    }

    @Override
    public String describeSourceLocation() {
        return getQualfiedName() + "()";
    }
}
