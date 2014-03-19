package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.AttrsStringLiteral;
import com.bedatadriven.rebar.cell.compiler.ast.Node;
import com.bedatadriven.rebar.cell.rebind.diagnostic.TypeMismatchException;

/**
 * Atomic values: strings. numbers, booleans
 */
public class AtomicExpr extends ExprNode implements Comparable<AtomicExpr> {
    private Object value;

    public AtomicExpr(String value) {
        this.value = value;
    }

    public AtomicExpr(Boolean value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if(value instanceof String) {
            return "'" + value + "'";
        } else {
            return "" + value;
        }
    }

    @Override
    public final <T> T accept(ExprVisitor<T> visitor) {
        return visitor.visitAtomicValue(this);
    }

    @Override
    public int compareTo(AtomicExpr o) {
        Comparable x = (Comparable) this.value;
        Comparable y = (Comparable) o.value;
        return x.compareTo(y);
    }

    public String asString() {
        return value.toString();
    }

}
