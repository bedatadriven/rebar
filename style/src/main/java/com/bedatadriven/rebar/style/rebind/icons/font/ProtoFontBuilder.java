package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;
import com.google.common.collect.Maps;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * Constructs a new SVG font from a series of icons
 */
public class ProtoFontBuilder {

  // hardcoded to the coordinate system used by FontAwesome but
  // we scale the individual fonts to fit the glyp
  public static final double HORIZ_ADV_X = 1536;
  public static final double UNITS_PER_EM = 1792;
  public static final double ASCENT = 1536;

  private double maxDescent = 0;

  private String newLine = "\n";


  private Map<Integer, ProtoGlyph> glyphs = Maps.newHashMap();


  public void addGlyph(int codePoint, IconSource source) {
    Shape glyphShape = source.getShape(IconSource.CoordinateSystem.FONT);

    // scale the ascent to match our  font
    double scale = ASCENT / source.getAscent();
    int descent = toInt(source.getDescent() * scale);
    int advance = toInt(source.getHorizontalAdvance() * scale);

    AffineTransform transform = new AffineTransform();
    transform.scale(scale, scale);

    // update the max descent if necessary
    maxDescent = Math.max(maxDescent, -descent);

    Shape transformedShape = transform.createTransformedShape(glyphShape);

    glyphs.put(codePoint, new ProtoGlyph(codePoint, transformedShape, advance));

  }


  public ProtoFont build() {
    return new ProtoFont(glyphs, (int) maxDescent);
  }

  private int toInt(double d) {
    return (int) Math.rint(d);
  }
}
