package com.bedatadriven.rebar.cell.rebind.eval;

import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by alex on 3/18/14.
 */
public class EvalException extends RuntimeException {

    private final List<ExprNode> stackTrace = Lists.newArrayList();


    public EvalException(ExprNode expr, String message, Exception e) {
        super(message, e);
        stackTrace.add(expr);
    }
    public EvalException(ExprNode expr, String message) {
        super(message);
        stackTrace.add(expr);
    }

    public void addToStackTrace(ExprNode expr) {
        stackTrace.add(expr);
    }

    public List<ExprNode> getExprStackTrace() {
        return stackTrace;
    }
}
