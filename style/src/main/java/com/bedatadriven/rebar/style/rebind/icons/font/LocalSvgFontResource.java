package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;

/**
 * Includes the SVG font within the HTML document
 */
public class LocalSvgFontResource implements FontResourceStrategy {

    @Override
    public String getName() {
        return "svg_local";
    }

    @Override
    public FontResources apply(ProtoFont font) {
        IconArtifacts artifacts = new IconArtifacts();
        artifacts.addInlineSvgDocument(font.buildLocalSvg());

        return new FontResources(font.defineLocalSvgSource(), artifacts);
    }

}
