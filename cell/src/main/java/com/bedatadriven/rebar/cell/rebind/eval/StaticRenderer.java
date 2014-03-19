package com.bedatadriven.rebar.cell.rebind.eval;

import com.bedatadriven.rebar.cell.rebind.dom.DomElement;
import com.bedatadriven.rebar.cell.rebind.expr.*;
import com.bedatadriven.rebar.cell.rebind.module.Cell;
import com.bedatadriven.rebar.cell.rebind.module.CellFunction;
import com.bedatadriven.rebar.cell.rebind.module.ElementType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Renders a Cell statically
 */
public class StaticRenderer {

    private StringBuilder html = new StringBuilder();

    private static final Logger LOGGER = Logger.getLogger(StaticRenderer.class.getName());

    public StaticRenderer() {
        LOGGER.setLevel(Level.ALL);
    }

    public void renderCell(Cell cell) {
        LOGGER.info("Rendering " + cell.getSimpleName());
        // Evaluate the render function
        TagExpr root = new TagExpr(cell);
        CellFunction render = cell.getRenderFunction();
        FunCallExpr renderCall = new FunCallExpr(new CellFunctionExpr(render));
        ExprNode rendered = renderCall.accept(new Evaluator(root));
        render(rendered);
    }

    public void renderTag(TagExpr tag) {
        LOGGER.info("Rendering " + tag.getElementType().getSimpleName());

        ElementType element = tag.getElementType();
        if(element instanceof DomElement) {
            html.append("<").append(element.getSimpleName());

            for(String attr : tag.getAttributeNames()) {
                ExprNode value = tag.getAttribute(attr);
                Evaluator evaluator = new Evaluator(tag);
                ExprNode result = value.accept(evaluator);

                html.append(" ").append(attr)
                        .append("='")
                        .append(formatAttributeValue(result))
                        .append("'");
            }

            if(tag.hasChildren()) {
                html.append(">");
                for(ExprNode child : tag.getChildren()) {
                    render(child);
                }
                html.append("</").append(element.getSimpleName()).append(">");
            } else {
                html.append("/>");
            }


        } else if(element instanceof Cell) {
            renderCell((Cell) element);
        } else {
            throw new EvalException(tag, "Unexpected value: " + tag);
        }
    }

    private void render(ExprNode child) {
        if(child instanceof AtomicExpr) {
            renderConstant((AtomicExpr)child);
        } else if(child instanceof TagExpr) {
            renderTag((TagExpr) child);
        } else {
            throw new EvalException(child, "Don't know how to render a " +
                    child.getClass().getSimpleName() + "; did something go wrong in the evaluation?");
        }
    }

    private void renderConstant(AtomicExpr child) {
        Object value = child.getValue();
        if(value instanceof String) {
            html.append(value);
        } else {
            throw new EvalException(child, "Cannot render " + child + " implicitly to HTML, use" +
                    "a converter");
        }
    }

    private String formatAttributeValue(ExprNode result) {
        if(result instanceof AtomicExpr) {
            Object value = ((AtomicExpr) result).getValue();
            if(value instanceof String) {
                return (String) value;
            }
        }
        throw new UnsupportedOperationException("Cannot coerce " + result + " to attribute value");
    }

    public String toHtml() {
        return html.toString();
    }
}
