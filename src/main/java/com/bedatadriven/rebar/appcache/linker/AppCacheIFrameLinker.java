/*
 * Copyright 2009-2010 BeDataDriven (alex@bedatadriven.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bedatadriven.rebar.appcache.linker;

import com.google.gson.stream.JsonWriter;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.*;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.linker.IFrameLinker;
import com.google.gwt.dev.util.Util;
import com.google.gwt.util.tools.Utility;

import java.io.*;
import java.util.*;

/**
 * Extends the standard <code>IFrameLinker</code> to generate manifests for
 * <code>ManagedResourceStores</code>, and to enable server-side selection of
 * *
 *
 *
 */
@LinkerOrder(Order.PRIMARY)
public final class AppCacheIFrameLinker extends IFrameLinker {


  public AppCacheIFrameLinker() {
  }

  @Override
  public String getDescription() {
    return "AppCache Linker";
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
    ArtifactSet toReturn = new ArtifactSet(artifacts);

    for (CompilationResult compilation : toReturn.find(CompilationResult.class)) {
      Collection<EmittedArtifact> compilationArtifacts = doEmitCompilation(logger, context, compilation);

      List<EmittedArtifact> toCache = new ArrayList<EmittedArtifact>();
      toCache.addAll(artifacts.find(EmittedArtifact.class));
      toCache.addAll(compilationArtifacts);

      toReturn.addAll(compilationArtifacts);
      toReturn.add(doEmitBootstrapScript(logger, context, artifacts, compilation));
      toReturn.add(doEmitManifest(logger, context, artifacts, compilation, new GearsManifestWriter(), toCache));
      toReturn.add(doEmitManifest(logger, context, artifacts, compilation, new Html5ManifestWriter(), toCache));
    }

    toReturn.add(emitPermutationMap(logger, context, artifacts));

    return toReturn;
  }


  private EmittedArtifact doEmitBootstrapScript(TreeLogger logger, LinkerContext context,
                                                ArtifactSet artifacts, CompilationResult compilation) throws UnableToCompleteException {

    String selectionScript = generateBootstrapScript(logger, context, artifacts, compilation);
    selectionScript = context.optimizeJavaScript(logger, selectionScript);

    /*
     * Last modified is important to keep hosted mode refreses from clobbering
     * web mode compiles. We set the timestamp on the hosted mode selection
     * script to the same mod time as the module (to allow updates). For web
     * mode, we just set it to now.
     */
    long lastModified;
    if (artifacts.find(CompilationResult.class).size() == 0) {
      lastModified = context.getModuleLastModified();
    } else {
      lastModified = System.currentTimeMillis();
    }

    return emitString(logger, selectionScript,
        compilation.getStrongName()
            + ".bootstrap.js", lastModified);
  }

  /**
   * The bootstrap script is an analog of the IFrameLinker's selection script,
   * execpt that permutation selection has already been done so it's mostly
   * straight-line code to load the body of the app.
   *
   */
  private String generateBootstrapScript(TreeLogger logger,
                                         LinkerContext context, ArtifactSet artifacts, CompilationResult result)
      throws UnableToCompleteException {

    // ICK,  copy-and-pasted from SelectionScriptLinker. Ideally work with
    // the GWT folks to make this more easily overridable.

    StringBuffer selectionScript;
    try {
      selectionScript = new StringBuffer(
          Utility.getFileFromClassPath(getSelectionScriptTemplate(logger,
              context)));
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to read selection script template",
          e);
      throw new UnableToCompleteException();
    }

    replaceAll(selectionScript, "__MODULE_FUNC__",
        context.getModuleFunctionName());
    replaceAll(selectionScript, "__MODULE_NAME__", context.getModuleName());

    int startPos;

    // Add external dependencies
    startPos = selectionScript.indexOf("// __MODULE_STYLES_END__");
    if (startPos != -1) {
      for (StylesheetReference resource : artifacts.find(StylesheetReference.class)) {
        String text = generateStylesheetInjector(resource.getSrc());
        selectionScript.insert(startPos, text);
        startPos += text.length();
      }
    }

    startPos = selectionScript.indexOf("// __MODULE_SCRIPTS_END__");
    if (startPos != -1) {
      for (ScriptReference resource : artifacts.find(ScriptReference.class)) {
        String text = generateScriptInjector(resource.getSrc());
        selectionScript.insert(startPos, text);
        startPos += text.length();
      }
    }

    // Possibly add permutations
    startPos = selectionScript.indexOf("// __PERMUTATIONS_END__");
    if (startPos != -1) {
      StringBuffer text = new StringBuffer();
      // Just one distinct compilation; no need to evaluate properties
      text.append("strongName = '" + result.getStrongName()  + "';");
      selectionScript.insert(startPos, text);
    }

    return selectionScript.toString();
  }

  @Override
  protected String getSelectionScriptTemplate(TreeLogger logger,
      LinkerContext context) {
    return "com/bedatadriven/rebar/appcache/linker/BootstrapTemplate.js";
  }

  /**
   * The permutation map is used on the server side to serve the appropriate permutation
   *
   */
  protected EmittedArtifact emitPermutationMap(TreeLogger logger,
                                               LinkerContext context, ArtifactSet artifacts)
      throws UnableToCompleteException {

    try {

      // Emit the selection script.
      StringWriter json = new StringWriter();
      JsonWriter writer = new JsonWriter(json);
      writer.setIndent("  ");

      SortedSet<CompilationResult> compilationResults = artifacts.find(CompilationResult.class);
      writer.beginArray();

      for(CompilationResult result : compilationResults) {
        for(SortedMap<SelectionProperty,String> map : result.getPropertyMap()) {
          writer.beginObject();
          writer.name("permutation").value(result.getStrongName());
          writer.name("properties");
          writer.beginObject();

          for(Map.Entry<SelectionProperty, String> property : map.entrySet()) {
            writer.name(property.getKey().getName());
            writer.value(property.getValue());
          }
          writer.endObject();
          writer.endObject();
        }
      }
      writer.endArray();
      return emitString(logger, json.toString(), "permutations");

    } catch(IOException e ) {
      logger.log(TreeLogger.Type.ERROR, "Error writing permutation map", e);
      throw new UnableToCompleteException();
    }
  }

  private EmittedArtifact doEmitManifest(TreeLogger logger, LinkerContext context,
                                         ArtifactSet artifacts,
                                         CompilationResult compilation,
                                         ManifestWriter writer,
                                         Collection<EmittedArtifact> artifactsToCache)
      throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.DEBUG, "Generating " + writer.getSuffix() + " contents",
        null);

    StringBuffer out = readManifestTemplate(logger, context, writer.getSuffix(), artifacts);

    // Generate the manifest entries
    appendEntries(logger, context, writer, artifactsToCache);



    // use the current time as the version number
    replaceAll(out, "__NAME__", context.getModuleName());
    replaceAll(out, "__VERSION__", generateTimestampVersion());
    replaceAll(out, "__ENTRIES__", writer.getEntries());

    /*
    * NB: It's tempting to use LinkerContext.optimizeJavaScript here, but the
    * JSON standard requires that the keys in the object literal will be
    * enclosed in double-quotes. In our optimized JS form, the double-quotes
    * would normally be removed.
    */
    return emitBytes(logger, Util.getBytes(out.toString()),
        compilation.getStrongName() + "." + writer.getSuffix());
  }

  private String generateTimestampVersion() {
    return Long.toString(new Date().getTime());
  }


  /**
   * Generate a string containing object literals for each manifest entry.
   */
  private void appendEntries(TreeLogger logger, LinkerContext context,
                             ManifestWriter writer,
                             Collection<EmittedArtifact> artifacts)
      throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.DEBUG, "Generating manifest entries",
        null);

    // add the bootstrap script (provided by the server)
    writer.appendEntry("bootstrap.js");

    for (EmittedArtifact artifact : artifacts) {
      if (artifact.isPrivate()) {
        logger.log(TreeLogger.DEBUG, "excluding private: " + artifact.getPartialPath());

        // These artifacts won't be in the module output directory
        continue;
      }

      String path = artifact.getPartialPath();

      // certain paths on the Windows platform (notably deferredjs stuff)
      // show up with backslahes, which throws an illegal escape sequence
      // error when the json is parsed.
      path = path.replace('\\', '/');

      logger.log(TreeLogger.DEBUG, "adding: " + path);
      writer.appendEntry(path);
    }
  }


  private StringBuffer readManifestTemplate(TreeLogger logger, LinkerContext context, String suffix, ArtifactSet artifacts) throws UnableToCompleteException {
    // first try to find a template provided in the module's public
    // folder
    for(PublicResource artifact : artifacts.find(PublicResource.class)) {
      if(artifact.getPartialPath().equals(context.getModuleName() + "." + suffix)) {
        return readAll(logger, artifact.getContents(logger));
      }
    }

    String defaultTemplate = "Default." + suffix;
    InputStream defaultIn = getClass().getResourceAsStream(defaultTemplate);
    if(defaultIn == null) {
      logger.log(TreeLogger.Type.ERROR, "Could not read default '" + defaultTemplate + "'");
      throw new UnableToCompleteException();
    }
    return readAll(logger, defaultIn);
  }

  private StringBuffer readAll(TreeLogger logger, InputStream in) throws UnableToCompleteException {
    StringBuffer out = new StringBuffer();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    try {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        out.append(line).append("\n");
      }
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to read manifest template", e);
      throw new UnableToCompleteException();
    }
    return out;
  }



}
