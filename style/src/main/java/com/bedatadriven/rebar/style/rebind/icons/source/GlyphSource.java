package com.bedatadriven.rebar.style.rebind.icons.source;

import com.kitfox.svg.Font;
import com.kitfox.svg.Glyph;
import com.kitfox.svg.MissingGlyph;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Reference to a Glyph within an existing (icon) font
 */
public class GlyphSource implements IconSource {

  private final Font font;
  private final int codePoint;
  private final Glyph glyph;

  public GlyphSource(Font font, int codePoint) {
    this.font = font;
    this.codePoint = codePoint;
    MissingGlyph foundGlyph = font.getGlyph(getUnicode());
    if (!(foundGlyph instanceof Glyph)) {
      throw new RuntimeException("Can't find glyph for code point 0x" + Integer.toHexString(codePoint));
    }
    this.glyph = (Glyph) foundGlyph;
  }

  private String getUnicode() {
    return new StringBuilder().appendCodePoint(codePoint).toString();
  }

  public Font getFont() {
    return font;
  }

  public int getCodePoint() {
    return codePoint;
  }

  public Glyph getGlyph() {
    return glyph;
  }

  @Override
  public Shape getShape(CoordinateSystem coordinateSystem) {
    // com.kitfox.svg.Glyph give us the glyph reflected to orient
    // to user space coordinates: scale(1, -1), so to get our
    // original shape back we need to undo the transformation

    switch (coordinateSystem) {
      case USER:
        return glyph.getShape();
      case FONT:
        return reflectY();
    }
    return glyph.getShape();
  }

  private Shape reflectY() {
    AffineTransform at = new AffineTransform();
    at.scale(1, -1);
    return at.createTransformedShape(glyph.getShape());
  }

  @Override
  public double getAscent() {
    return font.getFontFace().getAscent();
  }

  @Override
  public double getDescent() {
    return font.getFontFace().getDescent();
  }

  @Override
  public double getHorizontalAdvance() {
    return glyph.getHorizAdvX();
  }

}
