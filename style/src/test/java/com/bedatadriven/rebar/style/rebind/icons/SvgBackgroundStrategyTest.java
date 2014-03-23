package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.rebind.ConsoleTreeLogger;
import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;
import com.bedatadriven.rebar.style.rebind.icons.source.ImageSource;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SvgBackgroundStrategyTest {

    private TreeLogger logger = new ConsoleTreeLogger();

    @Test
    public void fromGlyph() throws IOException, UnableToCompleteException {


        SvgDocument heartSource = TestIcons.get("heart.svg");
        assertNotEmpty(heartSource.getShape());

        Icon imageIcon = new Icon("heart", new ImageSource(heartSource));
        assertNotEmpty(imageIcon.getSource().getShape(IconSource.CoordinateSystem.USER));


        Icon glyphIcon = TestIcons.awesome("fa-music");


        IconContext context = new IconContext();
        SvgBackgroundStrategy strategy = new SvgBackgroundStrategy();
        IconArtifacts artifacts = strategy.execute(logger, context, Arrays.asList(glyphIcon, imageIcon));

        System.out.println(artifacts.getStylesheet(""));

    }

    private void assertNotEmpty(Shape shape) {
        assertThat(shape.getBounds().isEmpty(), equalTo(false));
    }
}
