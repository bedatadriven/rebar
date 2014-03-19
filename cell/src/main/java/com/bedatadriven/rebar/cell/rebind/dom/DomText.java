package com.bedatadriven.rebar.cell.rebind.dom;

import com.bedatadriven.rebar.cell.rebind.module.ElementType;

/**
 * Created by alex on 3/19/14.
 */
public class DomText implements ElementType {


    DomText() {

    }

    @Override
    public String getSimpleName() {
        return "<text>";
    }
}
