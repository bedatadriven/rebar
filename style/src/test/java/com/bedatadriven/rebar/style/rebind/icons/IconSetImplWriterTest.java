package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.rebind.ConsoleTreeLogger;
import com.bedatadriven.rebar.style.rebind.icons.font.ExternalSvgFontResource;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IconSetImplWriterTest {

    private TreeLogger logger = new ConsoleTreeLogger();

    @Test
    public void staticRoot() throws IOException, UnableToCompleteException {
        IconContext iconContext = new IconContext();
        IconFontStrategy strategy = new IconFontStrategy(new ExternalSvgFontResource());
        IconArtifacts artifacts = strategy.execute(logger, iconContext,
                Collections.singletonList(TestIcons.awesome("fa-music")));

        String expr = IconSetImplWriter.styleSheetExpr(artifacts);

        assertThat(expr, equalTo("\"@font-face {font-family: 'F16f9dff6';src: url('\" + com.google.gwt.core.client.GWT.getModuleBaseForStaticFiles() + \"90DAA355F694646F3D938FD8FC1C2709.cache.svg#font-16f9dff66230b9e405725a8e17fd9c3e') format('svg');font-weight: normal;font-style: normal;}.icon{display: inline-block;font-family: F16f9dff6;font-style: normal;font-weight: normal;line-height: 1;-webkit-font-smoothing: antialiased;}.fa-music:before {content: '\\\\f000';}\""));
    }
}
