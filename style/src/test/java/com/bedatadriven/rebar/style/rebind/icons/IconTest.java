package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.ConsoleTreeLogger;
import com.google.common.base.Charsets;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.thirdparty.guava.common.io.Resources;
import org.apache.bcel.generic.ICONST;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;


public class IconTest {

    private ConsoleTreeLogger logger = new ConsoleTreeLogger();

    @Test
    public void test() throws IOException, TransformerException, UnableToCompleteException {

        IconSource source = new IconSource(
                Resources.toString(Resources.getResource(IconSource.class, "heart.svg"), Charsets.UTF_8));

        source.parse(logger);

        System.out.println(source.compactSvg());

        // make sure it can be reparsed!
        IconSource compact = new IconSource(source.compactSvg());
        compact.parse(logger);
    }

    @Test
    public void add() throws IOException, TransformerException, UnableToCompleteException {

        IconSource source = new IconSource(
                Resources.toString(Resources.getResource(IconSource.class, "add.svg"), Charsets.UTF_8));

        source.parse(logger);

        System.out.println(source.compactSvg());

        // make sure it can be reparsed!
        IconSource compact = new IconSource(source.compactSvg());
        compact.parse(logger);

        // and displayed...
        System.out.println(InlineSvgStrategy.asciiDataUrl(source));

    }
}
