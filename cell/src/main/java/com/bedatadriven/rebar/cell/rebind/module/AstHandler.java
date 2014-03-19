package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.compiler.tools.TerminalEvaluator;
import com.bedatadriven.rebar.cell.rebind.expr.AtomicExpr;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;

import java.util.List;

/**
 * Created by alex on 3/18/14.
 */
public class AstHandler extends ASTGrammarEvaluator implements TerminalEvaluator<CharSequence> {
    private String fileName;
    private String moduleName;
    private final LocationTracker locationTracker;
    boolean enableLineComment = true; //comments that starts with '//' clashes with xquery-like syntax

    public AstHandler(String fileName, String moduleName, LocationTracker locationTracker) {
        this.fileName = fileName;
        this.moduleName = moduleName;
        this.locationTracker = locationTracker;
    }

    @Override
    public IdToken id(CharSequence data) {
        return computeTokenAnnotation(new IdToken(data.toString()));
    }
    @Override
    public NullLiteralToken null_literal(CharSequence data) {
        return computeTokenAnnotation(new NullLiteralToken());
    }
    @Override
    public BoolLiteralToken bool_literal(CharSequence data) {
        return computeTokenAnnotation(new BoolLiteralToken(Boolean.parseBoolean(data.toString())));
    }
    @Override
    public ValueLiteralToken value_literal(CharSequence data) {
        String text = data.toString();
        Object value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            value = Double.parseDouble(text);
        }
        return computeTokenAnnotation(new ValueLiteralToken(value));
    }
    @Override
    public StringLiteralToken string_literal(CharSequence data) {
        return computeTokenAnnotation(new StringLiteralToken(data.subSequence(1, data.length() - 1).toString()));
    }
    @Override
    public XmlTextToken xml_text(CharSequence data) {
        return computeTokenAnnotation(new XmlTextToken(data.toString()));
    }
    @Override
    public XmlScriptTextToken xml_script_text(CharSequence data) {
        return computeTokenAnnotation(new XmlScriptTextToken(data.subSequence(0, data.length() - 8).toString()));
    }


    @Override
    public LcurlToken lcurl(CharSequence data) {
        return computeTokenAnnotation(new LcurlToken());
    }
    @Override
    public RcurlToken rcurl(CharSequence data) {
        return computeTokenAnnotation(new RcurlToken());
    }

    @Override
    public void multiline_comment(CharSequence data) {
        // comments
    }
    @Override
    public void oneline_comment(CharSequence data) {
        // comments
    }

    private <N extends Node> N computeTokenAnnotation(N node) {
        node.setLineNumberAttribute(locationTracker.getLineNumber());
        node.setColumnNumberAttribute(locationTracker.getColumnNumber());
        node.setFileNameAttribute(fileName);
        node.setModuleAttribute(moduleName);
        return node;
    }

    @Override
    protected void computeAnnotation(Node node) {
        List<Node> nodeList = node.nodeList();
        if (!nodeList.isEmpty()) {
            int nodeListSize = nodeList.size();
            for(int i=0; i<nodeListSize; i++) {
                Node firstNode = nodeList.get(i);
                if (firstNode == null) {
                    continue;
                }
                node.setLineNumberAttribute(firstNode.getLineNumberAttribute());
                node.setColumnNumberAttribute(firstNode.getColumnNumberAttribute());
                node.setFileNameAttribute(fileName);
                node.setModuleAttribute(moduleName);
                return;
            }
        }

        node.setLineNumberAttribute(locationTracker.getLineNumber());
        node.setColumnNumberAttribute(locationTracker.getColumnNumber());
        node.setFileNameAttribute(fileName);
        node.setModuleAttribute(moduleName);
    }
}
