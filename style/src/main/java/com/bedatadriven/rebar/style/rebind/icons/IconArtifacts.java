package com.bedatadriven.rebar.style.rebind.icons;

import com.google.common.collect.Lists;
import com.google.gwt.dev.util.Util;

import java.util.List;

/**
 * Artifacts created by an IconStrategy to support icon rendering
 */
public class IconArtifacts {


    public static class ExternalResource {
        private String name;
        private byte[] content;

        ExternalResource(String name, byte[] content) {
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public byte[] getContent() {
            return content;
        }
    }


    /**
     * CSS stylesheet defining at a minimum the styles for each icon
     */
    private StringBuilder stylesheet = new StringBuilder();


    /**
     * An inline SVG document
     */
    private List<String> inlineSvgDocuments = Lists.newArrayList();

    private List<ExternalResource> externalResources = Lists.newArrayList();

    public String getStylesheet() {
        return stylesheet.toString();
    }

    public void appendToStylesheet(String stylesheet) {
        this.stylesheet.append(stylesheet);
    }

    public List<String> getInlineSvgDocuments() {
        return inlineSvgDocuments;
    }

    public void addInlineSvgDocument(String source) {
        inlineSvgDocuments.add(source);
    }

    public List<ExternalResource> getExternalResources() {
        return externalResources;
    }

    /**
     *
     * @param content
     * @param extension
     * @return
     */
    public ExternalResource addExternalResource(byte[] content, String extension) {
        String name = Util.computeStrongName(content) + ".cache." + extension;
        ExternalResource resource = new ExternalResource(name, content);
        externalResources.add(resource);

        return resource;
    }


    public void add(IconArtifacts artifacts) {
        this.inlineSvgDocuments.addAll(artifacts.inlineSvgDocuments);
        this.externalResources.addAll(artifacts.externalResources);
        this.stylesheet.append(artifacts.stylesheet);
    }
}
