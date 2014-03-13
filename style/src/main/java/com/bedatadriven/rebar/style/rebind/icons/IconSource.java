package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.client.Icon;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;

import javax.xml.transform.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

/**
 * Represents a unique SVG image
 */
public class IconSource {

    /**
     * The SVG source
     */
    private String source;
    private Document doc;

    private final Set<String> textTags = Sets.newHashSet("text", "tspan", "tref");
    private final Set<String> tagsToIgnore = Sets.newHashSet("title", "metadata");

    private Set<String> prefixesUsed = Sets.newHashSet();

    public IconSource(String source) {
        this.source = source;
    }

    public static String sourceName(Icon iconDefinition) {
        String source = iconDefinition.value();
        if(!source.endsWith(".svg")) {
            source = source + ".svg";
        }
        return source;
    }

    public void parse(TreeLogger logger) throws UnableToCompleteException {

        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            doc = f.createDocument("http://activityinfo.org/icon.svg", new StringReader(source));

        } catch (IOException ex) {
            logger.log(TreeLogger.Type.ERROR, "IOException reading SVG", ex);
            throw new UnableToCompleteException();
        }
    }

    public String compactSvg() {
        StringBuilder sb = new StringBuilder();
        SVGSVGElement svg = (SVGSVGElement) doc.getDocumentElement();

        sb.append("<svg xmlns='http://www.w3.org/2000/svg\'");
//    No width/height as we want them to size to the container
//        appendAttributeIfPresent(svg, "width", sb);
//        appendAttributeIfPresent(svg, "height", sb);
        appendAttributeIfPresent(svg, "viewBox", sb);
        sb.append(">");

        appendChildren(svg, sb);

        sb.append("</svg>");

        return sb.toString();

    }

    private void appendChildren(SVGElement parent, StringBuilder sb) {
        for(int i = 0; i!= parent.getChildNodes().getLength();++i) {
            if(parent.getChildNodes().item(i) instanceof SVGElement) {
                SVGElement element = (SVGElement) parent.getChildNodes().item(i);
                if(!ignoreElement(element)) {
                    sb.append("<" + element.getTagName());
                    appendAttributes(element, sb);

                    if(isEmpty(element)) {
                        sb.append("/>");
                    } else {
                        sb.append(">");
                        appendChildren(element, sb);
                        sb.append("</" + element.getTagName() + ">");
                    }
                }
            }
        }
    }

    private boolean ignoreElement(SVGElement element) {
        if(tagsToIgnore.contains(element.getTagName())) {
            return true;
        }
        if("g".equals(element.getTagName()) && isEmpty(element)) {
            return true;
        }
        return false;
    }

    private void appendAttributeIfPresent(Element element, String name, StringBuilder sb) {
        String value = element.getAttribute(name);
        if(!Strings.isNullOrEmpty(value)) {
            sb.append(" ").append(name).append("='").append(value).append("'");
        }
    }

    /**
     * Use single quotes for attributes so it requires fewer bytes to escape.
     * @param element
     * @param sb
     */
    private void appendAttributes(Element element, StringBuilder sb) {
        for(int i=0;i!=element.getAttributes().getLength();++i) {
            Attr attr = (Attr) element.getAttributes().item(i);
            if(attr.getSpecified()) {
                sb.append(" ")
                    .append(attr.getName())
                    .append("='")
                    .append(condenseValue(element, attr)).append("'");
            }
        }
    }

    private String condenseValue(Element element, Attr attr) {
        // Can we abbreviate #AABBCC as #ABC ?
        if(attr.getValue().matches("#[A-F0-9]{6}")) {
            String rgb = attr.getValue();
            if(rgb.charAt(1) == rgb.charAt(2) &&
               rgb.charAt(3) == rgb.charAt(4) &&
               rgb.charAt(5) == rgb.charAt(6)) {

                return "#" + rgb.charAt(1) + rgb.charAt(3) + rgb.charAt(5);
            }
        }

        // Rejigger path data to encode better under URL encoding
        if(element instanceof SVGPathElement && attr.getName().equals("d")) {
            SVGPathElement path = (SVGPathElement) element;
            SVGPathSegList segList = path.getPathSegList();
            for(int i=0;i!=segList.getNumberOfItems();++i) {
                SVGPathSeg seg = segList.getItem(i);

            }
        }

        return attr.getValue();
    }

    private boolean isEmpty(Element element) {
        for(int i=0;i!=element.getChildNodes().getLength();++i) {
            if(element.getChildNodes().item(i) instanceof Element) {
                return false;
            }
        }
        return true;
    }
}
