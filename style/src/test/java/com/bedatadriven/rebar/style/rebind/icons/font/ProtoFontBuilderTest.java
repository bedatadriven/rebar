package com.bedatadriven.rebar.style.rebind.icons.font;

import com.bedatadriven.rebar.style.rebind.icons.source.GlyphSource;
import com.bedatadriven.rebar.style.rebind.icons.SvgDocument;
import com.bedatadriven.rebar.style.rebind.icons.TestIcons;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.kitfox.svg.Font;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Full test to rebuild fontawesome
 */
public class ProtoFontBuilderTest {


    @Test
    public void mixingFonts() throws IOException, UnableToCompleteException {
        SvgDocument fa = TestIcons.get("fontawesome.svg");

        ProtoFontBuilder newFont = new ProtoFontBuilder();

    }

    @Ignore
    @Test
    public void test() throws IOException, UnableToCompleteException {

        SvgDocument fa = TestIcons.get("fontawesome.svg");
        Font font = fa.getFonts().get(0);

        ProtoFontBuilder newFont = new ProtoFontBuilder();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("@font-face {\n");
        html.append("font-family: 'FontAwesome';\n");
        html.append("src: url('fa-test.svg#icon-font') format('svg');\n");
        html.append("font-weight: normal;\n");
        html.append("font-style: normal;\n");
        html.append("}\n");
        html.append("span.icon { font-family: FontAwesome; } ");
        html.append("</style></head><body><p>");

        Map<String, Integer> map = TestIcons.getFontAwesomeCodePoints();

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            int cp = entry.getValue();
            newFont.addGlyph(cp, new GlyphSource(font, cp));
            html.append("<p><span class='icon'>&#x" + Integer.toHexString(cp) + ";</span>");
            html.append("And some tex there... for (")
                    .append(Integer.toHexString(cp)).append(" = ")
                    .append(map.get(cp)).append(")</p>\n");
        }

        html.append("</body></html>");

        Files.write(html.toString(), new File("target/fa-test.html"), Charsets.UTF_8);
        Files.write(newFont.build().buildStandaloneSvgFontFile(), new File("target/fa-test.svg"), Charsets.UTF_8);
    }

}
