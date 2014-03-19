package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.rebind.diagnostic.CompilerException;
import com.bedatadriven.rebar.cell.rebind.diagnostic.SourceLocation;
import com.bedatadriven.rebar.cell.rebind.expr.*;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A component defines a
 */
public class Cell implements ElementType, Scope, SourceLocation {

    private final Module module;
    private final String cellName;
    private final String qualifiedCellName;
    private final Node body;

    private Map<String, CellMember> members = Maps.newHashMap();

    public Cell(Module module, String cellName, Node body) {
        this.module = module;
        this.cellName = cellName;
        this.qualifiedCellName = module.getQualifiedName() + "." + cellName;
        this.body = body;

        try {
            findMembers(body);
        } catch(Exception e) {
            throw CompilerException.wrap(this, e);
        }
        if(!(members.get("render") instanceof CellFunction)) {
            throw new RuntimeException(qualifiedCellName + " has no render function");
        }
    }

    public CellFunction getRenderFunction() {
        return (CellFunction) members.get("render");
    }


    private void findMembers(Node body) {
        body.accept(new AbstractVisitor() {
            @Override
            public Void visit(ExprLiteral expr_literal, Void _param) throws RuntimeException {
                return descend(expr_literal);
            }

            @Override
            public Void visit(LiteralArrayEntry literal_array_entry, Void _param) throws RuntimeException {
                return descend(literal_array_entry);
            }

            @Override
            public Void visit(ArrayEntry array_entry, Void _param) throws RuntimeException {

                ExprId idToken = (ExprId) array_entry.nodeList().get(0);
                String member = idToken.getId().getValue();

                try {
                    Node value = array_entry.nodeList().get(1);

                    if(value instanceof ExprFun) {
                        members.put(member, new CellFunction(Cell.this, member, (ExprFun) value));

                    } else {
                        members.put(member, new CellProperty(member,
                                ExprBuilder.build(Cell.this, value)));
                    }
                    return null;
                } catch(Exception e) {
                    throw CompilerException.wrap(Cell.this, e);
                }
            }

            @Override
            protected Void visit(Node node, Void _param) throws RuntimeException {
                throw new RuntimeException(node.getKind().toString());
            }
        }, null);
    }

    public Module getModule() {
        return module;
    }

    @Override
    public Cell resolveClass(String className) {
        return module.getCell(className);
    }

    @Override
    public ExprNode resolveName(String name) {
        CellMember member = getMember(name);
        if(member instanceof CellProperty) {
            return new CellPropertyRef(name, (CellProperty)member);
        } else {
            return new CellFunctionExpr((CellFunction)member);
        }
    }

    public CellMember getMember(String name) {
        if(!members.containsKey(name)) {
            throw new IllegalArgumentException(qualifiedCellName + " has no such member: " + name);
        }
        return members.get(name);
    }

    @Override
    public boolean isNameDefined(String name) {
        return members.containsKey(name);
    }

    @Override
    public String getSimpleName() {
        return cellName;
    }

    @Override
    public String describeSourceLocation() {
        return getModule().getQualifiedName() + "." + getSimpleName();
    }

    public String getQualifiedName() {
        return qualifiedCellName;
    }

    public boolean hasProperty(String varName) {
        return members.get(varName) instanceof CellProperty;
    }
}
