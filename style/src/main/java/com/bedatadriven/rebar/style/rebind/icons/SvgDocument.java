package com.bedatadriven.rebar.style.rebind.icons;

import com.google.common.collect.Lists;
import com.kitfox.svg.Font;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;

import java.awt.*;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

/**
 * An SVG document, which may be used as the source for one or more icons, either as an image
 * itself or as container of a font whose glyphs we will use.
 */
public class SvgDocument {

    private final String source;

    private SVGDiagram diagram;
    private List<Font> fonts = null;

    public SvgDocument(String svg) {
        this.source = svg;
        URI uri = SVGCache.getSVGUniverse().loadSVG(new StringReader(source), "myImage");
        diagram = SVGCache.getSVGUniverse().getDiagram(uri);
    }

    public String getSource() {
        return source;
    }

    public List<Font> getFonts() {
        if (fonts == null) {
            fonts = Lists.newArrayList();
            findFonts(diagram.getRoot());
        }
        return fonts;
    }

    private void findFonts(SVGElement parent) {
        for (int i = 0; i != parent.getNumChildren(); ++i) {
            SVGElement child = parent.getChild(i);
            if (child instanceof Font) {
                fonts.add((Font) child);
            } else {
                findFonts(child);
            }
        }
    }

    public Shape getShape() {
        return diagram.getRoot().getShape();
    }
}
