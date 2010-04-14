/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.worker.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.dev.util.Util;
import com.google.gwt.util.tools.Utility;

import java.io.IOException;
import java.util.SortedSet;

/**
 * @author Alex Bertram
 */
@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class WorkerLinker extends AbstractLinker {


  @Override
  public String getDescription() {
    return "Gears Worker Linker";
  }

  protected static void replaceAll(StringBuffer buf, String search,
                                   String replace) {
    int len = search.length();
    for (int pos = buf.indexOf(search); pos >= 0; pos = buf.indexOf(search,
        pos + 1)) {
      buf.replace(pos, pos + len, replace);
    }
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
      throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.Type.INFO, "Linking " + context.getModuleName() +
        " with WorkerLinker");

    SortedSet<CompilationResult> results = artifacts.find(CompilationResult.class);
    assertSinglePermutation(logger, results);

    CompilationResult result = results.first();
    assertNoFragments(logger, result);

    ArtifactSet toReturn = new ArtifactSet(artifacts);
    emitWorkerScript(logger, context, result, toReturn);
    emitTestHtml(logger, context, result, toReturn);

    return toReturn;
  }


  private void emitWorkerScript(TreeLogger logger, LinkerContext context, CompilationResult result, ArtifactSet toReturn) throws UnableToCompleteException {
    StringBuffer script = new StringBuffer();
    // the Compilation result includes only definitions, so put them first
    script.append(result.getJavaScript()[0]);

    StringBuffer template = getTemplate(logger, "WorkerTemplate.js");

    replaceAll(template, "__MODULE_NAME__", context.getModuleName());
    replaceAll(template, "__STRONG_NAME__", result.getStrongName());
    script.append(template.toString());

    String optimizedScript = context.optimizeJavaScript(logger, script.toString());

    // we want to output a single js file
    toReturn.add(emitBytes(logger, Util.getBytes(optimizedScript),
        context.getModuleName() + ".js"));
  }


  private void emitTestHtml(TreeLogger logger, LinkerContext context, CompilationResult result, ArtifactSet toReturn) throws UnableToCompleteException {
    StringBuffer template = getTemplate(logger, "WorkerTestTemplate.html");

    replaceAll(template, "__MODULE_NAME__", context.getModuleName());
    replaceAll(template, "__STRONG_NAME__", result.getStrongName());

    // we want to output a single js file
    toReturn.add(emitBytes(logger, Util.getBytes(template.toString()),
        context.getModuleName() + ".test.html"));
  }

  private StringBuffer getTemplate(TreeLogger logger, final String name) throws UnableToCompleteException {
    StringBuffer template;
    try {
      template = new StringBuffer(
          Utility.getFileFromClassPath("com/bedatadriven/rebar/worker/linker/" + name));
    } catch (IOException e) {
      logger.log(TreeLogger.Type.ERROR, "Could not load the template '" + name + "'");
      throw new UnableToCompleteException();
    }
    return template;
  }

  private void assertNoFragments(TreeLogger logger, CompilationResult result) {
    if (result.getJavaScript().length != 1) {
      logger.log(TreeLogger.Type.ERROR, "The CompilationResult must contain only one fragment. (Are you trying to use GWT.runAsync)?");
    }
  }

  private void assertSinglePermutation(TreeLogger logger, SortedSet<CompilationResult> results) throws UnableToCompleteException {
    if (results.size() != 1) {
      logger.log(TreeLogger.Type.ERROR, "Workers can only have one permutation. Make sure you're not " +
          "referencing DOM code, etc.");
      throw new UnableToCompleteException();
    }
  }
}
