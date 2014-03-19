package com.bedatadriven.rebar.cell.rebind.dom;

import com.google.common.collect.Maps;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import java.util.Map;

/**
 * Created by alex on 3/19/14.
 */
public class Dom {

    public static final Dom INSTANCE = new Dom();

    public static final DomText TEXT = new DomText();

    private Map<String, DomElement> elements = Maps.newHashMap();


    private Dom() {
        String[] tagNames = new String[] {
                "h1", "h2", "h3", "h4", "h5", "h6",
                "div", "span", "p", "a"
        };

        for(String tagName : tagNames) {
            elements.put(tagName, new DomElement(tagName));
        }
    }

    public boolean isDomElement(String name) {
        return elements.containsKey(name);
    }

    public DomElement getElement(String name) {
        DomElement domElement = elements.get(name);
        if(domElement == null) {
            throw new IllegalArgumentException(name);
        }
        return domElement;
    }

}
