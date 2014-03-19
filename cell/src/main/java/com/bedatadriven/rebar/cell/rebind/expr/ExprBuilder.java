package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.rebind.diagnostic.UnexpectedExprException;
import com.bedatadriven.rebar.cell.rebind.diagnostic.UnresolvedSymbolException;
import com.bedatadriven.rebar.cell.rebind.dom.Dom;
import com.bedatadriven.rebar.cell.rebind.expr.primitives.*;
import com.bedatadriven.rebar.cell.rebind.module.ElementType;
import com.google.common.collect.Lists;
import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;

import java.util.List;

/**
 * Creates an expr node from the low level AST
 */
public class ExprBuilder extends Visitor<ExprNode, Scope, RuntimeException> {

    private FunCallExpr unaryCall(PrimitiveFunExpr op, Node expr, Scope scope) {
        return new FunCallExpr(op, fromArg(scope, expr, 0));
    }

    private ExprNode binaryCall(PrimitiveFunExpr op, Node expr, Scope scope) {
        return new FunCallExpr(op,
                fromArg(scope, expr, 0),
                fromArg(scope, expr, 1));
    }

    
    @Override
    public ExprNode visit(ExprUnaryNot expr, Scope scope) throws RuntimeException {
        return unaryCall(new UnaryOperator(), expr, scope);
    }
    
    @Override
    public ExprNode visit(ExprUnaryPlus expr, Scope scope) throws RuntimeException {
        return unaryCall(new UnaryOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprUnaryMinus expr, Scope scope) throws RuntimeException {
        return unaryCall(new UnaryOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprPlus expr, Scope scope) throws RuntimeException {
        return binaryCall(new PlusOperator(), expr, scope);
    }


    @Override
    public ExprNode visit(ExprMinus expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprMult expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprDiv expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprMod expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprEq expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.EQUAL), expr, scope);
    }

    @Override
    public ExprNode visit(ExprNe expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.NOT_EQUAL), expr, scope);
    }

    @Override
    public ExprNode visit(ExprLt expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.LESS_THAN), expr, scope);
    }

    @Override
    public ExprNode visit(ExprLe expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.LESS_THAN_EQUAL_TO), expr, scope);
    }

    @Override
    public ExprNode visit(ExprGt expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.GREATER_THAN), expr, scope);
    }

    @Override
    public ExprNode visit(ExprGe expr, Scope scope) throws RuntimeException {
        return binaryCall(new ComparisonOperator(ComparisonOperator.Operator.NOT_EQUAL), expr, scope);
    }

    @Override
    public ExprNode visit(ExprOr expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    @Override
    public ExprNode visit(ExprAnd expr, Scope scope) throws RuntimeException {
        return binaryCall(new MinusOperator(), expr, scope);
    }

    private ExprNode fromArg(Scope scope, Node expr, int argumentIndex) {
        Node argument = expr.nodeList().get(argumentIndex);
        return argument.accept(this, scope);
    }

    @Override
    public ExprNode visit(ExprId expr_id, Scope scope) throws RuntimeException {
        String name = expr_id.getId().getValue();

        if(!scope.isNameDefined(name)) {
            throw new UnresolvedSymbolException(scope, expr_id.getId());
        }
        return scope.resolveName(name);
    }

    @Override
    public ExprNode visit(IdToken id, Scope scope) throws RuntimeException {
        return scope.resolveName(id.getValue());
    }

    @Override
    public ExprNode visit(ExprIf expr_if, Scope scope) throws RuntimeException {

        ExprNode condition = expr_if.getExpr().accept(this, scope);
        ExprNode ifTrue = expr_if.getExpr2().accept(this, scope);
        ExprNode ifFalse = expr_if.getExpr3().accept(this, scope);

        return new ConditionalExpr(condition, ifTrue, ifFalse);
    }


    @Override
    public ExprNode visit(PrimaryFieldAccess primary_field_access, Scope scope) throws RuntimeException {

        ExprNode value = fromArg(scope, primary_field_access, 0);
        IdToken field = primary_field_access.getId2();

        return new FieldAccessExpr(value, field.getValue());
    }

    @Override
    public ExprNode visit(PrimaryParens primary_parens, Scope scope) throws RuntimeException {
        return primary_parens.getExpr().accept(this, scope);
    }

    @Override
    public ExprNode visit(ExprXmls expr_xmls, Scope scope) throws RuntimeException {
        return expr_xmls.getXmls().accept(this, scope);
    }

    @Override
    public ExprNode visit(XmlsStartEndTag tag, Scope scope) throws RuntimeException {
        return parseTag(scope,
                tag.getId(),
                tag.getAttrs(),
                tag.getContent());
    }

    @Override
    public ExprNode visit(XmlsEmptyTag tag, Scope scope) throws RuntimeException {
        return parseTag(scope,
                tag.nodeList().get(0),
                tag.nodeList().get(1),
                null);
    }

    private ExprNode parseTag(Scope scope, Node tagNode, Node attr, Node content) {
        IdToken tagName = (IdToken) tagNode;
        ElementType type = resolveType(scope, tagName);

        AttrParser attrParser = new AttrParser(scope);
        attr.accept(attrParser, null);

        List<ExprNode> children = Lists.newArrayList();
        if(content != null) {
            addXmlContent(scope, content, children);
        }

        return new TagConstructorExpr(type, attrParser.getAttributes(), children);
    }

    private void addXmlContent(Scope scope, Node node, List<ExprNode> children) {

        if(node instanceof ContentEmpty) {
            return;
        }

//        System.out.println("ADDING child: ");
//        NodePrinter.dumpAstTree(node);


        addXmlContent(scope, node.nodeList().get(0), children);
        // leaf node
        Node leaf = node.nodeList().get(1);
        children.add(leaf.accept(this, scope));
    }

    private ElementType resolveType(Scope scope, IdToken id) {
        String tagName = id.getValue();
        if(Dom.INSTANCE.isDomElement(tagName)) {
            return Dom.INSTANCE.getElement(tagName);
        }
        try {
            return scope.resolveClass(tagName);
        } catch(IllegalArgumentException e) {
            throw new UnresolvedSymbolException(scope, id);
        }
    }

    public ExprNode visit(XmlTextToken xml_text, Scope _param)  {
        return new AtomicExpr(xml_text.getValue());
    }

    @Override
    protected ExprNode visit(DollarAccess dollar_access, Scope scope) throws RuntimeException {
        return dollar_access.nodeList().get(0).accept(this, scope);
    }

    @Override
    public ExprNode visit(ExprPrimary expr_primary, Scope scope) throws RuntimeException {
        return expr_primary.getPrimary().accept(this, scope);
    }

    @Override
    public ExprNode visit(ExprLiteral expr_literal, Scope scope) throws RuntimeException {
        return expr_literal.getLiteral().accept(this, scope);
    }

    @Override
    public ExprNode visit(LiteralSingle literal_single, Scope scope) throws RuntimeException {
        return literal_single.getSingleLiteral().accept(this, scope);
    }

    @Override
    public ExprNode visit(LiteralString literal_string, Scope scope) throws RuntimeException {
        return new AtomicExpr(literal_string.getStringLiteral().getValue());
    }

    @Override
    public ExprNode visit(LiteralBool literal_bool, Scope _param) throws RuntimeException {
        return new AtomicExpr(literal_bool.getBoolLiteral().getValue());
    }

    @Override
    public ExprNode visit(StringLiteralToken string_literal, Scope scope) throws RuntimeException {
        return new AtomicExpr(string_literal.getValue());
    }

    @Override
    public ExprNode visit(BoolLiteralToken bool_literal, Scope scope) throws RuntimeException {
        return new AtomicExpr(bool_literal.getValue());
    }


    @Override
    protected ExprNode visit(Node node, Scope scope) throws RuntimeException {
        UnexpectedExprException ex = new UnexpectedExprException(node,
                "Expecting a value expression such as:",
                " - number: 1, 3.4",
                " - string: 'hello world' or \"Hi there\"",
                " - variable: name, value, highlight",
                " - operations: (a + b) * 3",
                " - function calls: sin(42)",
                " - tags: <h1>$(name)</h1>",
                "         or <MyCell name='foo'/>");

        throw ex;
    }

    public static ExprNode build(Scope scope, Node node) {
       return node.accept(new ExprBuilder(), scope);
    }

}
