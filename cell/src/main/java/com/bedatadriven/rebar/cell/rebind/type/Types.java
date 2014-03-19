package com.bedatadriven.rebar.cell.rebind.type;


public class Types {

    public static final AtomicType STRING = new AtomicType("string");


    public ExprType valueOf(Class clazz) {
        if(clazz.equals(String.class)) {
            return STRING;
        }
        throw new UnsupportedOperationException();
    }

}
