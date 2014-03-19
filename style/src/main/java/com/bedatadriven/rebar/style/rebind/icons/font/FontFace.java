package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.IconArtifacts;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Helper class for building font face declarations
 */
public class FontFace {

    private final ProtoFont font;
    private List<String> sources = Lists.newArrayList();

    public FontFace(ProtoFont font) {
        this.font = font;
    }

    public FontFace withSource(FontSource source) {
        sources.add(source.toString());
        return this;
    }

    public String toCSS() {

        if(sources.isEmpty()) {
            throw new RuntimeException("No sources defined");
        }

        return new StringBuilder()
           .append("@font-face {")
           .append("font-family: '").append(font.getFontFamily()).append("';")
           .append("src: ")
           .append(Joiner.on(", ").join(sources))
           .append(";").append("font-weight: normal;")
           .append("font-style: normal;")
           .append("}")
           .toString();
    }

}
