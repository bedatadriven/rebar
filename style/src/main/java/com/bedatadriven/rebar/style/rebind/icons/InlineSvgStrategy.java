package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.client.impl.IconUtil;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Inlines the svg as background with a data url.
 */
public class InlineSvgStrategy implements IconStrategy {

    private final BaseEncoding base64encoder = BaseEncoding.base64();

    @Override
    public void appendCommonDeclarations(StringBuilder cssExpr) {
        cssExpr.append("position: relative;");
        cssExpr.append("top: 1px;");
        cssExpr.append("display: inline-block;");
        cssExpr.append("line-height: 1;");
        cssExpr.append("width: 1em;");
        cssExpr.append("height: 1em;");
        cssExpr.append("background-repeat: no-repeat;");
    }

    @Override
    public void appendDeclarations(TreeLogger logger, IconSource source, StringBuilder cssExpr)
            throws UnableToCompleteException {
        try {
            String svg = source.compactSvg();
            String ascii = asciiDataUrl(source);

            String base64 = "data:image/svg+xml;base64," + base64encoder.encode(svg.getBytes(Charsets.US_ASCII));

            String shortest = ascii.length() < base64.length() ? ascii : base64;

            cssExpr.append("background-image:url('" + shortest + "');");
        } catch(Exception e) {
            logger.log(TreeLogger.Type.ERROR, "Transformation of XML failed: " + e.getMessage(), e);
            throw new UnableToCompleteException();
        }
    }

    public static String asciiDataUrl(IconSource source) throws UnsupportedEncodingException {
        return "data:image/svg+xml;charset=US-ASCII," +
                        URLEncoder.encode(source.compactSvg(), "ASCII").replace("+", "%20");
    }
}
