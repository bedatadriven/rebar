package com.bedatadriven.rebar.style.rebind.icons.source;

import com.bedatadriven.rebar.style.rebind.icons.SvgDocument;
import com.bedatadriven.rebar.style.rebind.icons.TestIcons;
import com.bedatadriven.rebar.style.rebind.icons.font.ProtoFont;
import com.bedatadriven.rebar.style.rebind.icons.font.ProtoFontBuilder;
import com.bedatadriven.rebar.style.rebind.icons.font.ProtoGlyph;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.kitfox.svg.SVGRoot;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;


public class ImageSourceTest {

  @Test
  public void standalone() throws IOException, UnableToCompleteException {

    SvgDocument svg = TestIcons.get("heart.svg");
    SVGRoot root = svg.getDiagram().getRoot();
    Shape shape = root.getShape();
    assertThat(shape.getBounds().getWidth(), equalTo(32d));
    assertThat(shape.getBounds().getHeight(), equalTo(29d));


    System.out.println(TestIcons.awesome("fa-music").getSource()
        .getShape(IconSource.CoordinateSystem.FONT).getBounds());

    ImageSource source = new ImageSource(svg);
    shape = source.getShape(IconSource.CoordinateSystem.FONT);
    System.out.println(shape.getBounds());
    assertThat(shape.getBounds().getX(), equalTo(0d));
    assertThat(shape.getBounds().getY(), lessThan(0d));

    ProtoFontBuilder fontBuilder = new ProtoFontBuilder();
    fontBuilder.addGlyph(0xf001, source);
    ProtoFont font = fontBuilder.build();

    ProtoGlyph glyph = font.getGlyphs().get(0xf001);
    System.out.println(glyph.getShape().getBounds());


  }
}
