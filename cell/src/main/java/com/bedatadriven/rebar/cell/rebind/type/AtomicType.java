package com.bedatadriven.rebar.cell.rebind.type;

/**
 * Created by alex on 3/18/14.
 */
public class AtomicType extends ExprType {

    private final String name;

    public AtomicType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "atomic:" + name;
    }
}
