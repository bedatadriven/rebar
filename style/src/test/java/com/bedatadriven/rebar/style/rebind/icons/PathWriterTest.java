package com.bedatadriven.rebar.style.rebind.icons;

import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Test;

import java.awt.geom.Path2D;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PathWriterTest {

  @Test
  public void quadraticShortcut() throws IOException, UnableToCompleteException {

    Path2D path = new Path2D.Float();
    path.moveTo(1792, 32);
    path.quadTo(1792, -34, 1745, -81);
    path.quadTo(1698, -128, 1632, -128);

    PathWriter writer = new PathWriter();
    String d = writer.write(path);

    assertThat(d, equalTo("M1792 32q0-66-47-113t-113-47"));
  }

  @Test
  public void pencil() {
    //<glyph unicode="&#xf040;" horiz-adv-x="1536" d="M363 0 l91 91-235 235-91-91V128h128V0h107Zm523 928q0 22-22 22-10 0-17-7l-542-542q-7-7-7-17q0-22 22-22q10 0 17 7l542 542q7 7 7 17Zm-54 192l416-416-832-832H0v416Zm1515 736q0-53-37-90l-166-166-416 416l166 165q36 38 90 38q53 0 91-38l235-234q37-39 37-91Z
    // <glyph unicode='&#xf040;'                    d='M363 0 l91 91l-235 235l-91 -91v-107h128v-128h107zM886 928q0 22 -22 22q-10 0 -17 -7l-542 -542q-7 -7 -7 -17q0 -22 22 -22q10 0 17 7l542 542q7 7 7 17zM832 1120l416 -416l-832 -832h-416v416zM1515 1024q0 -53 -37 -90l-166 -166l-416 416l166 165q36 38 90 38 q53 0 91 -38l235 -234q37 -39 37 -91z' />

  }
}
