package com.bedatadriven.rebar.style.rebind.icons.font;

import java.awt.*;


public class ProtoGlyph {

  private int codePoint;
  private Shape shape;
  private int horizontalAdvancement;

  public ProtoGlyph(int codePoint, Shape shape, int horizontalAdvancement) {
    this.codePoint = codePoint;
    this.shape = shape;
    this.horizontalAdvancement = horizontalAdvancement;
  }

  public Shape getShape() {
    return shape;
  }

  public int getHorizontalAdvancement() {
    return horizontalAdvancement;
  }

  public int getCodePoint() {
    return codePoint;
  }
}
