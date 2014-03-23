package com.bedatadriven.rebar.style.rebind.icons.source;

import com.bedatadriven.rebar.style.rebind.icons.SvgDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * An Icon Source which is provided by a standalone SVG image
 */
public class ImageSource implements IconSource {

    private final SvgDocument source;

    public ImageSource(SvgDocument source) {
        this.source = source;
    }

    @Override
    public Shape getShape(CoordinateSystem coordinateSystem) {
        switch(coordinateSystem) {
            case USER:
                return source.getShape();

            case FONT:
                return transformToFontCoordinateSystem();
        }
        throw new UnsupportedOperationException();
    }

    private Shape transformToFontCoordinateSystem() {
        Shape shape = source.getShape();

        // reflect to get in glyph coordinate system
        // and translate so that it's on the base line
        AffineTransform at = new AffineTransform();
        at.scale(1, -1);
        Rectangle bounds = shape.getBounds();
        at.translate(-bounds.getMinX(), - (bounds.getHeight() - bounds.getMinY()));
        return at.createTransformedShape(shape);
    }

    @Override
    public double getAscent() {
        // assume the image is flush with the baseline...
        return source.getShape().getBounds().getHeight();
    }

    @Override
    public double getDescent() {
        return 0;
    }

    @Override
    public double getHorizontalAdvance() {
        return source.getShape().getBounds().getWidth();
    }

    @Override
    public String toString() {
        return "ImageSource(" + source.getShape().getBounds() + ")";
    }
}
