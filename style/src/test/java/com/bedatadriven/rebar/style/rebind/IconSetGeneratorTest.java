package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.rebind.icons.*;
import com.bedatadriven.rebar.style.rebind.icons.font.ExternalSvgFontResource;
import com.bedatadriven.rebar.style.rebind.icons.font.LocalSvgFontResource;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.bedatadriven.rebar.style.rebind.icons.TestIcons.awesome;


public class IconSetGeneratorTest {

    private File outputDir;

    @Before
    public void setUp() {
        outputDir = new File("target/icon-tests");
        outputDir.mkdirs();
    }
    
    @Test
    public void test() throws IOException, UnableToCompleteException {
        
        List<IconStrategy> strategies = Lists.newArrayList();
        strategies.add(new SvgBackgroundStrategy());
        strategies.add(new IconFontStrategy(new ExternalSvgFontResource()));
        strategies.add(new IconFontStrategy(new LocalSvgFontResource()));

        List<Icon> icons = Lists.newArrayList();
        icons.add(TestIcons.imageIcon("heart"));
        icons.add(TestIcons.imageIcon("remove"));

        icons.add(awesome("fa-music"));
        icons.add(awesome("fa-search"));
        icons.add(awesome("fa-heart"));
        icons.add(awesome("fa-star"));
        icons.add(awesome("fa-user"));
        icons.add(awesome("fa-film"));
        icons.add(awesome("fa-cog"));
        icons.add(awesome("fa-home"));
        icons.add(awesome("fa-file-o"));
        icons.add(awesome("fa-clock-o"));
        icons.add(awesome("fa-repeat"));
        icons.add(awesome("fa-refresh"));
        icons.add(awesome("fa-list-alt"));
        icons.add(awesome("fa-lock"));
        icons.add(awesome("fa-bookmark"));
        icons.add(awesome("fa-forward"));
        icons.add(awesome("fa-exclamation-triangle"));
        icons.add(awesome("fa-facebook-square"));
        icons.add(awesome("fa-camera-retro"));
        icons.add(awesome("fa-github-square"));
        icons.add(awesome("fa-certificate"));
        icons.add(awesome("fa-sitemap"));
        icons.add(awesome("fa-terminal"));
        icons.add(awesome("fa-microphone"));
        icons.add(awesome("fa-microphone-slash"));
        icons.add(awesome("fa-shield"));
        icons.add(awesome("fa-bitbucket"));
        icons.add(awesome("fa-bitbucket-square"));
        icons.add(awesome("fa-windows"));
        icons.add(awesome("fa-android"));
        icons.add(awesome("fa-linux"));
        icons.add(awesome("fa-dribbble"));
        icons.add(awesome("fa-skype"));
        icons.add(awesome("fa-foursquare"));
        icons.add(awesome("fa-trello"));
        icons.add(awesome("fa-female"));
        icons.add(awesome("fa-male"));

        for(IconStrategy strategy : strategies) {
            IconArtifacts artifacts = strategy.execute(new ConsoleTreeLogger(), new IconContext(), icons);

            writeHtml(icons, strategy, artifacts);
            writeExternalResources(artifacts);
        }
    }

    private void writeExternalResources(IconArtifacts artifacts) throws IOException {
        for(IconArtifacts.ExternalResource resource : artifacts.getExternalResources()) {
            Files.write(resource.getContent(), new File(outputDir, resource.getName()));
        }
    }

    private void writeHtml(List<Icon> icons, IconStrategy strategy, IconArtifacts artifacts) throws IOException {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append(artifacts.getStylesheet(""));
        html.append("</style></head><body>");

        for(String svgElement : artifacts.getInlineSvgDocuments()) {
            html.append(svgElement);
        }

        for(Icon icon : icons) {
            html.append("<p><span class='icon " + icon.getClassName() + "'></span> " +
                    "And Here's to " + icon.getClassName() + "!</p>");
        }

        html.append("</body></html>");

        Files.write(html.toString(), new File(outputDir, "icons_" + strategy.getName() + ".html"), Charsets.UTF_8);
    }
}
