package com.bedatadriven.rebar.style.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;

public class StylesheetInjector {

	private static boolean injected = false;

	public static boolean ensureInjected() {
		if(!injected) {
			String url = GWT.getModuleBaseForStaticFiles() + "/" + GWT.getPermutationStrongName() + ".cache.css";
			getHead().appendChild(createElement(url));
			injected = true;
			return true;
		}
		return false;
	}

	private static LinkElement createElement(String url) {
		LinkElement link = Document.get().createLinkElement();
		link.setHref(url);
		link.setRel("stylesheet");
		link.setType("text/css");
		return link;
	}

	private static HeadElement getHead() {
		Element elt = Document.get().getElementsByTagName("head").getItem(0);
		assert elt != null : "The host HTML page does not have a <head> element"
				+ " which is required by StyleInjector";
		return HeadElement.as(elt);
	}

	/**
	 * Utility class.
	 */
	private StylesheetInjector() {
	}
}
