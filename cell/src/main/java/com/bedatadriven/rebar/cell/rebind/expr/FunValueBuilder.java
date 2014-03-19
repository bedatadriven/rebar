package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.*;
import com.bedatadriven.rebar.cell.rebind.diagnostic.AstLocation;
import com.bedatadriven.rebar.cell.rebind.diagnostic.CompilerException;
import com.bedatadriven.rebar.cell.rebind.diagnostic.SourceLocation;
import com.bedatadriven.rebar.cell.rebind.diagnostic.UnexpectedExprException;
import com.bedatadriven.rebar.cell.rebind.module.*;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.bedatadriven.rebar.cell.rebind.module.Compiler.*;

/**
 * Constructs the (return) value of a function
 */
public class FunValueBuilder extends AbstractVisitor implements Scope {

    /**
     * Our function
     */
    private CellFunction function;

    private ExprNode value = null;


    /**
     * We allow local variable definitions for convenience but their
     * values cannot change
     */
    private Map<String, ExprNode> definitions = Maps.newHashMap();

    /**
     * Functions can have a return value of "mutations"; that is a list of changes
     * to make to the Cell's state. We provide an 'update x = y' syntax to
     * make writing these functions more naturally.
     */
    private Map<String, ExprNode> mutations = Maps.newHashMap();

    public FunValueBuilder(CellFunction function) {
        this.function = function;
    }

    public ExprNode build(Block block) {
        block.accept(this, null);

        if(value == null && mutations.isEmpty()) {
            SourceLocation location;
            if(block.nodeList().size() > 0) {
               location = new AstLocation(block.nodeList().get( block.nodeList().size() - 1));
            } else {
                location = new AstLocation(block);
            }
            throw new CompilerException(location, function.getName() + " has no return value.",
                    "Functions must return a value; make sure that all paths end with",
                    " a return() statement.");

        } else if(value != null) {
            return value;

        } else {
            return new MutatorExpr(mutations);
        }
    }

    @Override
    public Void visit(Block block, Void _param) throws RuntimeException {
        return descend(block);
    }

    @Override
    public Void visit(InstrDecl instr_decl, Void _param) throws RuntimeException {
        return instr_decl.getDeclaration().accept(this, _param);
    }

    @Override
    public Void visit(DeclarationLet declaration_let, Void _param) throws RuntimeException {

        String varName = declaration_let.getLetDeclaration().getId().getValue();

        if(definitions.containsKey(varName)) {
            throw new CompilerException(new AstLocation(declaration_let), "Redefinition of '" + varName + "'",
                    "You can only declare a variable once in a given function using the let statement,",
                    "and because " + LANGUAGE_NAME + " is pure, you may not change the variable's value.")
                    .addNamedSourceLocation("previous definition", definitions.get(varName).getSourceLocation());
        }
        if(isNameDefined(varName)) {
            throw new CompilerException(new AstLocation(declaration_let), "Redefinition of '" + varName + "'")
                    .addNamedSourceLocation("previous declaration", resolveName(varName).getSourceLocation());
        }
        definitions.put(varName, ExprBuilder.build(this, declaration_let.getLetDeclaration().getExpr()));

        return null;
    }

    @Override
    public Void visit(DeclarationUpdate update_declaration, Void _param) throws RuntimeException {

        String varName = update_declaration.getUpdateDeclaration().getId().getValue();

        if(!function.getCell().hasProperty(varName)) {
            throw new CompilerException(new AstLocation(update_declaration),
                    function.getCell().getQualifiedName() + " has no property named '" + varName + "'");
        }

        if(definitions.containsKey(varName)) {
            throw new CompilerException(new AstLocation(update_declaration), "Property '" + varName +
                    "' has already been updated by this function.",
                    "You can only update a property once in a given function using the update statement.",
                    "This is because any updates would override the previous update, so something must " +
                            "be wrong!")
                    .addNamedSourceLocation("previous definition", definitions.get(varName).getSourceLocation());
        }

        ExprNode updatedValue = ExprBuilder.build(this, update_declaration.getUpdateDeclaration().getExpr());

        mutations.put(varName, updatedValue);

        return null;
    }

    @Override
    public Void visit(InstrIf instr_if, Void _param) throws RuntimeException {

        // we have to tread carefully here to make sure if variables are defined in the individual
        // branches that they have an equivalent in the paired branch, otherwise
        // we end up with "nothings" which we haven't included in the language yet

//        System.out.println(instr_if);
//
//        ExprNode condition = ExprBuilder.build(this, this, instr_if.getExpr());
//        ExprNode ifTrue = ExprBuilder.build(this, this, instr_if.getInstr());

        throw new CompilerException(new AstLocation(instr_if), "If statements are not yet implemented",
                "You can use the ternary operator instead. For example, replace:",
                "",
                "    if(x > 3) {",
                "        return 'blue;'",
                "    } else {",
                "        return 'green;",
                "    }" +
                "",
                "with:" +
                "",
                "    return (x > 3) ? 'blue' : 'green'");
    }

    @Override
    public Void visit(InstrReturn instr_return, Void _param) throws RuntimeException {

        if(!mutations.isEmpty()) {

            throw new CompilerException(new AstLocation(instr_return), "Update-valued functions cannot return additional values",
                    "Some functions like event handlers are what we call 'update-valued', meaning that actually",
                            "what they do is return a list of changes to be made.",
                            "For these functions, their return value *is* this list of updates, so if you use the",
                            "update statement, you can't then return an additional value");

        }

        this.value = ExprBuilder.build(this, instr_return.nodeList().get(0));
        return null;
    }

    @Override
    public ExprNode resolveName(String name) {

        if(definitions.containsKey(name)) {
            return new LocalDefRefExpr(name, definitions.get(name));
        }
        if(function.getCell().isNameDefined(name)) {
            return function.getCell().resolveName(name);
        }

        //declaringCell.getMember(name.getValue());
        throw new IllegalArgumentException(name);
    }


    @Override
    public Cell resolveClass(String className) {
        return function.getCell().resolveClass(className);
    }

    @Override
    public boolean isNameDefined(String name) {
        return definitions.containsKey(name) ||
                function.getCell().isNameDefined(name);
    }

    @Override
    protected Void visit(Node node, Void _param) {
        throw new UnexpectedExprException(node);
    }
}
