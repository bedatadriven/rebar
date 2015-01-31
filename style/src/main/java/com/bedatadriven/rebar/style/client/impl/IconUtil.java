package com.bedatadriven.rebar.style.client.impl;


import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class IconUtil {

  public static void injectSvgDocument(String innerSvg) {
    Element svg = Document.get().createElement("svg");
    svg.setPropertyString("style", "display:none;");
    svg.setInnerHTML(innerSvg);

    Document.get().getBody().insertFirst(svg);

  }
}
