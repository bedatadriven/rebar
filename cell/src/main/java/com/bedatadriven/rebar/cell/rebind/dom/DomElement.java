package com.bedatadriven.rebar.cell.rebind.dom;

import com.bedatadriven.rebar.cell.rebind.module.ElementType;

public class DomElement implements ElementType {
    private String name;

    public DomElement(String name) {
        this.name = name;
    }

    @Override
    public String getSimpleName() {
        return name;
    }

}
