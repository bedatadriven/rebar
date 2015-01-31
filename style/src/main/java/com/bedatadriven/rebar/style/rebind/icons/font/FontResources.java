package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;

/**
 * Created by alex on 3/16/14.
 */
public class FontResources {

  private FontSource source;
  private IconArtifacts artifacts;

  public FontResources(FontSource source, IconArtifacts artifacts) {
    this.source = source;
    this.artifacts = artifacts;
  }

  public FontSource getSource() {
    return source;
  }

  public IconArtifacts getArtifacts() {
    return artifacts;
  }
}
