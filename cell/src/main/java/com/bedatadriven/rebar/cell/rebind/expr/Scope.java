package com.bedatadriven.rebar.cell.rebind.expr;

import com.bedatadriven.rebar.cell.compiler.ast.IdToken;
import com.bedatadriven.rebar.cell.rebind.module.Cell;

/**
 * Created by alex on 3/18/14.
 */
public interface Scope {


    Cell resolveClass(String className);

    ExprNode resolveName(String name);

    boolean isNameDefined(String name);

}
