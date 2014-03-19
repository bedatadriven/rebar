package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

/**
 * Created by alex on 3/18/14.
 */
public class TypeMismatchException extends RuntimeException {
    public TypeMismatchException(ExprNode value, Class expected, Class required) {

    }
}
