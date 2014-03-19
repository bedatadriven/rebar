package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.rebind.diagnostic.SourceLocation;
import com.bedatadriven.rebar.cell.rebind.type.ExprType;

/**
 * Root of our expression tree.
 *
 * <p>Examples of expressions include:
 *
 * <ul>
 *     <li>Simple constants: {@code 42}, {@code "Hello world"} </li>
 *     <li>References to local definitions or cell state: {@code starred}</li>
 *     <li>Function calls: {@code a * b} or {@code sin(x)}</li>
 * </ul>
 */
public abstract class ExprNode {

    private final SourceLocation sourceLocation;

    public ExprNode() {
        this.sourceLocation = SourceLocation.UNKNOWN;
    }

    public ExprNode(SourceLocation location) {
        this.sourceLocation  = location;
    }

    public abstract <T> T accept(ExprVisitor<T> visitor);

    public ExprType getType() {
        throw new UnsupportedOperationException();
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }
}
