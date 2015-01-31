package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;

/**
 *
 */
public class ExternalSvgFontResource implements FontResourceStrategy {

  @Override
  public String getName() {
    return "svg_external";
  }

  @Override
  public FontResources apply(ProtoFont protoFont) {
    String fontFileSource = protoFont.buildStandaloneSvgFontFile();

    IconArtifacts artifacts = new IconArtifacts();
    IconArtifacts.ExternalResource fontFile =
        artifacts.addExternalResource(
            fontFileSource.getBytes(), "svg");

    return new FontResources(protoFont.defineExternalSvgSource(fontFile), artifacts);
  }
}
