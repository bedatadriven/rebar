package com.bedatadriven.rebar.cell.rebind.expr;

/**
 * Visitor for
 */
public class ExprVisitor<R> {

    public R visitConstructor(TagConstructorExpr tag) {
        return unhandled(tag);
    }

    public R visitConditionalExpr(ConditionalExpr expr) {
        return unhandled(expr);
    }

    public R visitFieldAccess(FieldAccessExpr expr) {
        return unhandled(expr);
    }

    public R visitFunctionCall(FunCallExpr expr) {
        return unhandled(expr);

    }

    public R visitFunValue(FunValExpr expr) {
        return unhandled(expr);

    }

    public R visitAtomicValue(AtomicExpr expr) {
        return unhandled(expr);

    }

    public R visitLocalReference(LocalDefRefExpr expr) {
        return unhandled(expr);

    }

    public R visitCellPropertyRef(CellPropertyRef cellPropertyRef) {
        return unhandled(cellPropertyRef);
    }

    public R visitTag(TagExpr tagExpr) {
        return unhandled(tagExpr);
    }

    public R unhandled(ExprNode expr) {
        return null;
    }

    public R visitMutation(MutatorExpr expr) {
        return unhandled(expr);
    }
}
