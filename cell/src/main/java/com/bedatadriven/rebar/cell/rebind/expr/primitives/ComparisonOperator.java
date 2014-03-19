package com.bedatadriven.rebar.cell.rebind.expr.primitives;

import com.bedatadriven.rebar.cell.rebind.diagnostic.CompilerException;
import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;

import java.util.List;


public class ComparisonOperator extends PrimitiveFunExpr {

    public enum Operator {
        EQUAL {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult == 0;
            }
        },
        NOT_EQUAL {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult != 0;
            }
        },
        LESS_THAN {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult < 0;
            }
        },
        LESS_THAN_EQUAL_TO {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult <= 0;
            }
        },
        GREATER_THAN {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult > 0;
            }
        },
        GREATER_THAN_EQUAL_TO {
            @Override
            boolean evaluate(int comparisonResult) {
                return comparisonResult >= 0;
            }
        };

        abstract boolean evaluate(int comparisonResult);
    }

    private final Operator operator;

    public ComparisonOperator(Operator op) {
        this.operator = op;
    }

    @Override
    public AtomicExpr apply(List<ExprNode> arguments) {
        if(arguments.size() != 2) {
            throw new PrimitiveException("Expected two arguments for " + getClass().getName());
        }
        ExprNode x = arguments.get(0);
        ExprNode y = arguments.get(1);

        if(!(x instanceof AtomicExpr) || !(y instanceof AtomicExpr)) {
            throw new PrimitiveException("Expected two constant values as arguments, got: " +
                    x + ", " + y);
        }

        AtomicExpr ax = (AtomicExpr) x;
        AtomicExpr ay = (AtomicExpr) y;

        int comparison = ax.compareTo(ay);

        boolean result = operator.evaluate(comparison);

        return new AtomicExpr(result);
    }

    @Override
    public String toString() {
        return operator.name().toLowerCase();
    }
}
