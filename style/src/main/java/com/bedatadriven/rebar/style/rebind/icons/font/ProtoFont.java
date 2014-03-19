package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;
import com.bedatadriven.rebar.style.rebind.icons.PathWriter;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.gwt.dev.util.Util;
import com.kitfox.svg.pathcmd.PathUtil;

import java.awt.Shape;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.toHexString;

/**
 * Proto font that can be built in a number of other formats
 */
public class ProtoFont {
    private final Map<Integer, ProtoGlyph> glyphs;
    private int descent;
    private final String strongName;
    private String newLine = "\n";

    public ProtoFont(Map<Integer, ProtoGlyph> glyphs, int descent) {
        this.glyphs = glyphs;
        this.descent = descent;
        this.strongName = calculateStrongName();
    }

    private String calculateStrongName() {
        Hasher hasher = Hashing.md5().newHasher();
        List<Integer> codePoints = Lists.newArrayList(glyphs.keySet());
        Collections.sort(codePoints);

        for(Integer codePoint : codePoints) {
            ProtoGlyph glyph = glyphs.get(codePoint);
            hasher.putInt(codePoint);
            hasher.putInt(glyph.getHorizontalAdvancement());
            hasher.putString(PathWriter.toPathData(glyph.getShape()), Charsets.UTF_8);
        }
        return hasher.hash().toString();
    }

    public String getFontId() {
        return "font-" + strongName;
    }

    public String getFontFamily() {
        return "F" + strongName.substring(0, 8);
    }

    public FontFace declareFontFace() {
        return new FontFace(this);
    }

    public FontSource defineLocalSvgSource() {
        return new FontSource("local('" + getFontFamily() + "')");
    }

    public FontSource defineExternalSvgSource(IconArtifacts.ExternalResource fontFile) {
        return new FontSource(url(fontFile, getFontId()) + format(FontFormat.SVG));
    }


    public FontSource defineExternalSource(IconArtifacts.ExternalResource resource, FontFormat format) {
        return new FontSource(url(resource) + format(format));
    }

    private static String url(IconArtifacts.ExternalResource fontFile, String id) {
        return "url('" + fontFile.getName() + "#" + id + "')";
    }


    private static String url(IconArtifacts.ExternalResource fontFile) {
        return "url('" + fontFile.getName() + "')";
    }

    private static String format(FontFormat format) {
        return " format('" + format.name().toLowerCase().replace('_', '-') + "')";
    }

    public String buildStandaloneSvgFontFile() {
        StringBuilder svg = new StringBuilder();
        svg.append("<?xml version='1.0' standalone='no'?>").append(newLine);
        svg.append("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' ")
                .append("'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'>\n").append(newLine);
        svg.append("<svg xmlns='http://www.w3.org/2000/svg'>").append(newLine);

        appendDefs(svg);

        svg.append("</svg>").append(newLine);

        return svg.toString();
    }


    public String buildLocalSvg() {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg style='display: none;'>");
        appendDefs(svg);
        svg.append("</svg>");
        return svg.toString();
    }

    private void appendDefs(StringBuilder svg) {
        svg.append("<defs>").append(newLine);
        svg.append("<font id='").append(getFontId()).append("' horiz-adv-x='1536'>").append(newLine);
        svg.append("<font-face font-family='").append(getFontFamily())
                .append("' units-per-em='1792' ascent='1536' descent='")
                .append(-descent)
                .append("'/>")
                .append(newLine);
        svg.append("<missing-glyph horiz-adv-x='448' />").append(newLine);
        svg.append("<glyph unicode=' '  horiz-adv-x='448' />").append(newLine);

        for(Map.Entry<Integer, ProtoGlyph> entry : glyphs.entrySet()) {
            int codePoint = entry.getKey();
            ProtoGlyph glyph = entry.getValue();

            // Add the glyph to our list
            svg.append("<glyph");
            svg.append(" unicode=\"&#x").append(toHexString(codePoint)).append(";\"");
            svg.append(" horiz-adv-x=\"").append(glyph.getHorizontalAdvancement()).append("\"");
            svg.append(" d=\"").append(PathWriter.toPathData(glyph.getShape())).append("\"/>");
            svg.append(newLine);
        }

        svg.append("</font>");
        svg.append("</defs>");
    }

    public Map<Integer, ProtoGlyph> getGlyphs() {
        return glyphs;
    }
}
