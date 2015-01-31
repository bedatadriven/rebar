package com.bedatadriven.rebar.style.rebind.gss;

import com.bedatadriven.rebar.style.rebind.ConsoleTreeLogger;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class GssCompilerTest {

  private TreeLogger logger = new ConsoleTreeLogger();

  @Test
  public void collectClassNames() throws UnableToCompleteException {

    String css =
        ".foobar { font-weight: bold; }\n" +
            ".bazinga { color: blue; }\n";

    GssTree tree = new GssCompiler().compile(logger, css);


    assertTrue(tree.getClassNames().contains("foobar"));
    assertTrue(tree.getClassNames().contains("bazinga"));

  }
}
