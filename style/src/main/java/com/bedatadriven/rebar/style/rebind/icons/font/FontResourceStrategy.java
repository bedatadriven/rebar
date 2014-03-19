package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;

/**
 * Provides a strategy for the format and reference of a font file
 */
public interface FontResourceStrategy {

    String getName();

    FontResources apply(ProtoFont font);

}
