package com.bedatadriven.rebar.cell.rebind.module;

/**
 * A named member of a Cell, either a function or a static value
 */
public class CellMember {

    private String name;

    public CellMember(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
