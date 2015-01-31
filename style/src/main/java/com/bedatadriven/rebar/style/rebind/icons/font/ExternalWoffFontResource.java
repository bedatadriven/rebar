package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;
import com.google.common.base.Charsets;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.tools.conversion.woff.WoffWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ExternalWoffFontResource implements FontResourceStrategy {
  @Override
  public String getName() {
    return "external_woff";
  }

  @Override
  public FontResources apply(ProtoFont font) {

//        Engine engine = Engine.getSingletonInstance();
//        GlyphFile newGlyph = engine.createNewGlyph();
////        EContour counter;
////        newGlyph.addContour(counter);


    String svgSource = font.buildStandaloneSvgFontFile();

    Font[] fonts;
    try {
      fonts = FontFactory.getInstance().loadFonts(svgSource.getBytes(Charsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException("Sftnly could not load svg font", e);
    }
    if (fonts.length != 1) {
      throw new RuntimeException("Expected 1 font, got " + fonts.length);
    }

    WritableFontData woffData = new WoffWriter().convert(fonts[0]);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      woffData.copyTo(baos);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write WOFF data", e);
    }

    IconArtifacts artifacts = new IconArtifacts();
    IconArtifacts.ExternalResource resources = artifacts.addExternalResource(baos.toByteArray(), "woff");

    return new FontResources(font.defineExternalSource(resources, FontFormat.WOFF), artifacts);
  }
}
