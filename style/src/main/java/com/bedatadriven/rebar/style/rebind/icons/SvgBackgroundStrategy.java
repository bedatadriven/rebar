package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.gwt.core.ext.TreeLogger;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Renders icons using an SVG background image encoded as Data URI
 */
public class SvgBackgroundStrategy implements IconStrategy {

    private final BaseEncoding base64encoder = BaseEncoding.base64();

    @Override
    public String getName() {
        return "svg_bg_data_uri";
    }

    @Override
    public IconArtifacts execute(TreeLogger logger, IconContext context, List<Icon> icons) {
        IconArtifacts artifacts = new IconArtifacts();
        artifacts.appendToStylesheet(composeStylesheet(icons));
        return artifacts;
    }

    private String composeStylesheet( List<Icon> icons) {
        StringBuilder css = new StringBuilder();
        css.append(".icon {");
        css.append("position: relative;");
        css.append("top: 1px;");
        css.append("display: inline-block;");
        css.append("line-height: 1;");
        css.append("width: 1em;");
        css.append("height: 1em;");
        css.append("background-repeat: no-repeat;");
        css.append("}");

        for (Icon icon : icons) {
            css.append(".").append(icon.getClassName()).append("{");
            css.append("background-image:url('").append(toDataUri(icon)).append("');");
            css.append("}");
        }
        return css.toString();
    }

    private String toDataUri(Icon icon) {
        return asciiDataUrl(toImage(icon));
    }

    private String toImage(Icon icon) {

        // First transform our shape to a view box so that it has a minimum of 1000 of
        Shape shape = icon.getSource().getShape(IconSource.CoordinateSystem.USER);

        if(shape.getBounds().isEmpty()) {
            throw new IllegalArgumentException("Icon " + icon.getAccessorName() + " with source " +
                    icon.getSource() + " is empty");
        }

        Rectangle bounds = shape.getBounds();
        double scale = 1000d / Math.max(bounds.getWidth(), bounds.getHeight());

        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        transform.translate(-bounds.getMinX(), -bounds.getMinY());
        Shape transformedShape = transform.createTransformedShape(shape);

        int viewBoxWidth = (int)Math.rint(transformedShape.getBounds().getWidth());
        int viewBoxHeight = (int)Math.rint(transformedShape.getBounds().getHeight());

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns='http://www.w3.org/2000/svg\' viewBox='0 0 ")
            .append(viewBoxWidth).append(" ")
            .append(viewBoxHeight).append("'>");

        svg.append("<path fill='#000' d='");
        svg.append(PathWriter.toPathData(transformedShape));
        svg.append("'/></svg>");

        return svg.toString();
    }

    public static String asciiDataUrl(String svg)  {
        try {
            return "data:image/svg+xml;charset=US-ASCII," +
                    URLEncoder.encode(svg, "ASCII").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
