package com.bedatadriven.rebar.style.rebind.icons.source;

import java.awt.*;

/**
 * Common interface to icon sources coming from standalone SVG
 * files and from glyphs defined in existing font files
 */
public interface IconSource {

  Shape getShape(CoordinateSystem system);

  double getAscent();

  double getDescent();

  double getHorizontalAdvance();

  enum CoordinateSystem {
    USER,
    FONT
  }

}
