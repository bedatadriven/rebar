package com.bedatadriven.rebar.style.rebind.icons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * Writes a compact path representation of an SVG path.
 */
public class PathWriter {
  private float[] coords = new float[6];

  private float currentX = Float.NaN;
  private float currentY = Float.NaN;

  private float lastControlPointX = Float.NaN;
  private float lastControlPointY = Float.NaN;

  private char lastCommand = 0;
  private int lastSegType = -1;

  private boolean firstPoint = true;

  private StringBuilder d = new StringBuilder();

  public static String toPathData(Shape shape) {
    StringBuilder d = new StringBuilder();
    PathWriter writer = new PathWriter();
    writer.appendTo(shape.getPathIterator(new AffineTransform()), d);
    return d.toString();
  }

  public String write(Path2D path) {
    StringBuilder d = new StringBuilder();
    appendTo(path.getPathIterator(new AffineTransform()), d);
    return d.toString();
  }

  public void appendTo(PathIterator it, StringBuilder d) {
    this.d = d;

    while (!it.isDone()) {
      int segType = it.currentSegment(coords);

      switch (segType) {
        case PathIterator.SEG_MOVETO:
          appendMoveTo();
          break;

        case PathIterator.SEG_LINETO:
          appendLineTo();
          break;

        case PathIterator.SEG_CUBICTO:
          appendCubic();
          break;

        case PathIterator.SEG_QUADTO:
          appendQuad();
          break;

        case PathIterator.SEG_CLOSE:
          d.append("Z");
          break;
      }
      it.next();
      lastSegType = segType;
    }
  }


  private void appendMoveTo() {
    appendAbsolute('M', coords[0], coords[1]);

    currentX = coords[0];
    currentY = coords[1];
  }

  private void appendLineTo() {
    int dx = (int) Math.rint(coords[0] - currentX);
    int dy = (int) Math.rint(coords[1] - currentY);

    if (dx == 0) {
      appendRelativeVHIfShorter('v', coords[1], currentY);

    } else if (dy == 0) {
      appendRelativeVHIfShorter('h', coords[0], currentX);

    } else {
      appendRelative('l', coords[0], coords[1]);

    }
    currentX = coords[0];
    currentY = coords[1];
  }


  private void appendQuad() {
    if (canUseShortCut(PathIterator.SEG_QUADTO)) {

      appendRelative('t', coords[2], coords[3]);

    } else {
      appendRelative('q', coords[0], coords[1]);
      appendRelative(coords[2], coords[3]);
    }

    lastControlPointX = coords[0];
    lastControlPointY = coords[1];
    currentX = coords[2];
    currentY = coords[3];
  }

  private void appendCubic() {
    if (canUseShortCut(PathIterator.SEG_CUBICTO)) {

      appendRelative('s', coords[2], coords[3]);
      appendRelative(coords[4], coords[5]);

    } else {
      appendRelative('c', coords[0], coords[1]);
      appendRelative(coords[2], coords[3]);
      appendRelative(coords[4], coords[5]);
    }

    lastControlPointX = coords[2];
    lastControlPointY = coords[3];
    currentX = coords[4];
    currentY = coords[5];
  }

  private boolean canUseShortCut(int curveType) {
    // The first control point is assumed to be the reflection of the second control point on the
    // previous command relative to the current point. (If there is no previous command or
    // if the previous command was not an C, c, S or s, assume the first control point is
    // coincident with the current point.) (x2,y2) is the second control point (i.e.,
    // the control point at the end of the curve).

    float expectedX1 = currentX;
    float expectedY1 = currentY;
    if (lastSegType == curveType) {
      expectedX1 += (currentX - lastControlPointX);
      expectedY1 += (currentY - lastControlPointY);
    }

    return toInt(expectedX1) == toInt(coords[0]) &&
        toInt(expectedY1) == toInt(coords[1]);
  }

  private void appendRelativeVHIfShorter(char relativeCommand, float f, float last) {
    String rel = toString(f - last);
    String abs = toString(f);

    if (abs.length() < rel.length()) {
      char absCommand = Character.toUpperCase(relativeCommand);
      appendCoord(absCommand, f);

    } else {
      appendCoord(relativeCommand, f - last);
    }
  }

  private void appendAbsolute(char absoluteCommand, float x, float y) {
    d.append(absoluteCommand);
    appendCoord(x);
    appendCoord(y);
    lastCommand = absoluteCommand;
  }

  private void appendRelative(char command, float x, float y) {
    appendCoord(command, x - currentX);
    appendCoord(y - currentY);
    lastCommand = command;
  }

  private void appendRelative(float x, float y) {
    appendCoord(x - currentX);
    appendCoord(y - currentY);
  }

  private void appendCoord(char command, float f) {
    int i = (int) Math.rint(f);
    if (command != lastCommand || i >= 0) {
      d.append(command);
    }
    appendCoord(f);
    lastCommand = command;
  }

  private void appendCoord(float f) {
    int i = toInt(f);
    char lastChar = d.charAt(d.length() - 1);
    if (i >= 0 && Character.isDigit(lastChar)) {
      d.append(' ');
    }
    d.append(i);
  }

  private int toInt(float f) {
    return (int) Math.rint(f);
  }

  private String toString(float f) {
    return Integer.toString((int) Math.rint(f));
  }
}
