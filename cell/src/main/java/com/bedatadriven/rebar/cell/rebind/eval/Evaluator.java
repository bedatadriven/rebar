package com.bedatadriven.rebar.cell.rebind.eval;

import com.bedatadriven.rebar.cell.rebind.expr.*;
import com.bedatadriven.rebar.cell.rebind.expr.primitives.PrimitiveFunExpr;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Simple evaluator suitable for testing purposes.
 */
public class Evaluator extends ExprVisitor<ExprNode> {

    private final TagExpr currentTag;

    public Evaluator(TagExpr tag) {
        this.currentTag = tag;
    }

    @Override
    public ExprNode visitFunctionCall(FunCallExpr expr) {
        if(expr.getFunction() instanceof PrimitiveFunExpr) {
            List<ExprNode> evaluatedArgs = Lists.newArrayList();
            for(ExprNode argument : expr.getArguments()) {
                evaluatedArgs.add(eval(argument));
            }
            return ((PrimitiveFunExpr) expr.getFunction()).apply(evaluatedArgs);


        } else if(expr.getFunction() instanceof CellFunctionExpr) {

            CellFunctionExpr cfe = (CellFunctionExpr) expr.getFunction();
            return eval(cfe.getFunction().getBody());

        } else {
            throw new UnsupportedOperationException("todo");
        }
    }

    public ExprNode eval(ExprNode expr) {
        try {
            return expr.accept(this);
        } catch(EvalException e) {
            e.addToStackTrace(expr);
            throw e;
        } catch(Exception e) {
            throw new EvalException(expr, "Evaluation failed with exception: " + e.getMessage(), e);
        }
    }

    @Override
    public ExprNode visitTag(TagExpr tagExpr) {
        return tagExpr; // constant value, already evaluated
    }

    @Override
    public ExprNode visitLocalReference(LocalDefRefExpr expr) {
        return eval(expr.getValue());
    }

    @Override
    public ExprNode visitAtomicValue(AtomicExpr expr) {
        return expr;
    }

    @Override
    public ExprNode visitConstructor(TagConstructorExpr ctor) {

        Map<String, ExprNode> evaluatedAttributes = Maps.newHashMap();
        List<ExprNode> evaluatedChildren = Lists.newArrayList();

        for(String name : ctor.getAttributeNames()) {
            evaluatedAttributes.put(name, eval(ctor.getAttribute(name)));
        }

        for(ExprNode child : ctor.getChildren()) {
            evaluatedChildren.add(eval(child));
        }

        return new TagExpr(ctor.getElementType(), evaluatedAttributes, evaluatedChildren);
    }

    @Override
    public ExprNode visitConditionalExpr(ConditionalExpr expr) {
        ExprNode condition = eval(expr.getCondition());
        if(isTrue(condition)) {
            return eval(expr.getIfTrue());
        } else {
            return expr.getIfFalse();
        }
    }

    @Override
    public ExprNode visitCellPropertyRef(CellPropertyRef cellPropertyRef) {
        if(currentTag.isAttributeDefined(cellPropertyRef.getName())) {
            return currentTag.getAttribute(cellPropertyRef.getName());
        }
        return eval(cellPropertyRef.getProperty().getInitialValue());
    }

    private boolean isTrue(ExprNode expr) {
        if(expr instanceof AtomicExpr) {
            Object value = ((AtomicExpr) expr).getValue();
            if(value instanceof Boolean) {
                return (Boolean)value;
            }

        }
        throw new UnsupportedOperationException("Cannot coerce " + expr + " to boolean");
    }

    @Override
    public ExprNode unhandled(ExprNode expr) {
        throw new EvalException(expr, "Not implemented");
    }
}
