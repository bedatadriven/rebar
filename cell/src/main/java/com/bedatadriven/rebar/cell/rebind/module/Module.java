package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.rebind.diagnostic.UnexpectedExprException;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Cell modules are single compilation units that define components
 */
public class Module extends Visitor<Void, Void, RuntimeException> {

    private String qualifiedName;
    private Map<String, Cell> cells = Maps.newHashMap();

    public Module(String qualifiedName, Script script) {
        this.qualifiedName = qualifiedName;

        script.accept(new AbstractVisitor() {

            @Override
            public Void visit(ScriptMember script_member, Void _param) throws RuntimeException {
                return descend(script_member);
            }

            @Override
            public Void visit(ScriptScriptMember script_script_member, Void _param) throws RuntimeException {
                return descend(script_script_member);
            }

            @Override
            public Void visit(MemberInstr member_instr, Void _param) throws RuntimeException {
                return descend(member_instr);
            }

            @Override
            public Void visit(InstrAssign assignment, Void _param) throws RuntimeException {
                IdToken lhs = (IdToken) assignment.getAssignment().nodeList().get(0);
                Node rhs = assignment.getAssignment().nodeList().get(1);

                cells.put(lhs.getValue(), new Cell(Module.this, lhs.getValue(), rhs));

                return null;
            }

            @Override
            protected Void visit(Node node, Void _param) throws RuntimeException {
                throw new UnexpectedExprException(node,
                        "Expecting a component definition, in the form of:",
                        "PageCell = {",
                        "    active: false; // initial state",
                        "    render: function() { } , ",
                        "}");
            }
        }, null);
    }


    public String getQualifiedName() {
        return qualifiedName;
    }

    public Collection<Cell> getCells() {
        return cells.values();
    }

    public Cell getCell(String cellName) {
        Cell cell = cells.get(cellName);
        if(cell == null) {
            throw new IllegalArgumentException(cellName);
        }
        return cell;
    }


}
