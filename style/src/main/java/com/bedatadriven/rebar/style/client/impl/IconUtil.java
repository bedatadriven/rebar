package com.bedatadriven.rebar.style.client.impl;


import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class IconUtil {

    public static native void toDataUrl(String svg) /*-{
        return "data:image/svg+xml;charset=US-ASCII," + encodeURIComponent(svg);
    }-*/;


//    public static void injectSvg(String svg) {
//        Document.get().createElement("svg")
//        BodyElement body = Document.get().getBody();
//        body.inf
//    }
}
