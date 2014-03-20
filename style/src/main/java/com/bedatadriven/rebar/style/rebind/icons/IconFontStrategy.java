package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.rebind.icons.font.FontResourceStrategy;
import com.bedatadriven.rebar.style.rebind.icons.font.FontResources;
import com.bedatadriven.rebar.style.rebind.icons.font.ProtoFont;
import com.bedatadriven.rebar.style.rebind.icons.font.ProtoFontBuilder;
import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;
import com.google.common.collect.Maps;
import com.google.gwt.core.ext.TreeLogger;

import java.util.List;
import java.util.Map;

/**
 * Renders icons using an "icon font"
 */
public class IconFontStrategy implements IconStrategy {

    public static final int BEGIN_PRIVATE_USE_AREA = 0xf000;

    private final FontResourceStrategy resourceStrategy;

    public IconFontStrategy(FontResourceStrategy resourceStrategy) {
        this.resourceStrategy = resourceStrategy;
    }

    @Override
    public String getName() {
        return "icon_font_" + resourceStrategy.getName();
    }

    @Override
    public IconArtifacts execute(TreeLogger logger, IconContext context, List<Icon> icons) {

        Map<IconSource, Integer> codePoints = assignCodePoints(icons);
        ProtoFont font = buildFont(icons, codePoints);

        // Delegate to figure out where this font will end up...
        FontResources resources = resourceStrategy.apply(font);


        // Add our
        IconArtifacts artifacts = new IconArtifacts();
        artifacts.add(resources.getArtifacts());
        artifacts.appendToStylesheet(font
                .declareFontFace()
                .withSource(resources.getSource())
                .toCSS());

        artifacts.appendToStylesheet(baseStyle(context, font));
        artifacts.appendToStylesheet(iconStyles(icons, codePoints));
        return artifacts;
    }

    private ProtoFont buildFont(List<Icon> icons, Map<IconSource, Integer> codePoints) {
        ProtoFontBuilder fontBuilder = new ProtoFontBuilder();
        for(Icon icon : icons) {
            int codePoint = codePoints.get(icon.getSource());
            fontBuilder.addGlyph(codePoint, icon.getSource());
        }
        return fontBuilder.build();
    }

    private Map<IconSource, Integer> assignCodePoints(List<Icon> icons) {
        // First assign code points to the icons
        int nextCodePoint = BEGIN_PRIVATE_USE_AREA;

        Map<IconSource, Integer> codePoints = Maps.newHashMap();
        for(Icon icon : icons) {
            if(!codePoints.containsKey(icon.getSource())) {
                int codePoint = nextCodePoint++;
                codePoints.put(icon.getSource(), codePoint);
            }
        }
        return codePoints;
    }


    private String baseStyle(IconContext context, ProtoFont font) {
        // Now finally the CSS
        StringBuilder css = new StringBuilder();

        // The Base Class
        css.append(".").append(context.getBaseClassName()).append("{");
        css.append("display: inline-block;");
        css.append("font-family: ").append(font.getFontFamily()).append(";");
        css.append("font-style: normal;");
        css.append("font-weight: normal;");
        css.append("line-height: 1;");
        css.append("-webkit-font-smoothing: antialiased;");
        css.append("}");
        return css.toString();
    }

    private String iconStyles(List<Icon> icons, Map<IconSource, Integer> codePoints) {
        StringBuilder css = new StringBuilder();
        for(Icon icon : icons) {
            int codePoint = codePoints.get(icon.getSource());
            css.append(".").append(icon.getClassName()).append(":before {");
            css.append("content: ").append(quote(escape(codePoint))).append(";");
            css.append("}");
        }
        return css.toString();
    }

    private String escape(int codePoint) {
        return "\\" + Integer.toHexString(codePoint).toLowerCase();
    }

    private String quote(String name) {
        return "'" + name + "'";
    }
}
