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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.google.gson.stream.JsonWriter;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.PublicResource;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.impl.ResourceInjectionUtil;
import com.google.gwt.core.linker.IFrameLinker;
import com.google.gwt.dev.util.Util;
import com.google.gwt.util.tools.Utility;

/**
 * Extends the standard <code>IFrameLinker</code> to generate manifests for
 * <code>ManagedResourceStores</code>, and to enable server-side selection of
 * permutations.
 *
 */
@LinkerOrder(Order.PRIMARY)
public final class AppCacheIFrameLinker extends IFrameLinker {

  private boolean hostedMode = false;

  public AppCacheIFrameLinker() {
  }

  @Override
  public String getDescription() {
    return "AppCache Linker";
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext linkerContext, ArtifactSet artifacts) throws UnableToCompleteException {
    
    Collection<CompilationResult> compilationResults = artifacts.find(CompilationResult.class);
    
    hostedMode = compilationResults.size() == 0;
    
    if(hostedMode) {
      // if we are being run in hosted mode, revert entirely to the IFrameLinker
      
      return super.link(logger, linkerContext, artifacts);
    
    } else {
      
      ArtifactSet toReturn = new ArtifactSet(artifacts);
  
      for (CompilationResult compilation : compilationResults) {
        PermutationContext context = new PermutationContext(linkerContext, artifacts, compilation);
  
        Collection<Artifact<?>> compilationArtifacts = doEmitCompilation(logger, linkerContext, compilation, artifacts);
  
        context.addToCache(artifacts.find(EmittedArtifact.class));
        context.addToCache(emittedArtifacts(compilationArtifacts));
  
        toReturn.addAll(compilationArtifacts);
        toReturn.add(doEmitBootstrapScript(logger, context, artifacts));
        toReturn.add(doEmitManifest(logger, context, new GearsManifestWriter()));
        toReturn.add(doEmitManifest(logger, context, new Html5ManifestWriter()));
      }
  
      toReturn.add(emitPermutationMap(logger, linkerContext, artifacts));
     
      return toReturn;
    }
  }
  
  private Collection<EmittedArtifact> emittedArtifacts(Collection<Artifact<?>> artifacts) {
	  List<EmittedArtifact> list = new ArrayList<EmittedArtifact>();
	  for(Artifact<?> artifact : artifacts) {
		  if(artifact instanceof EmittedArtifact) {
			  list.add((EmittedArtifact) artifact);
		  }
	  }
	  return list;
  }


  private EmittedArtifact doEmitBootstrapScript(TreeLogger logger, PermutationContext context, ArtifactSet artifacts) throws UnableToCompleteException {

    String selectionScript = generateBootstrapScript(logger, context, artifacts);
    selectionScript = context.optimizeJavaScript(logger, selectionScript);

    /*
     * Last modified is important to keep hosted mode refreshes from clobbering
     * web mode compiles. We set the timestamp on the hosted mode selection
     * script to the same mod time as the module (to allow updates). For web
     * mode, we just set it to now.
     */
    long lastModified;
    if (context.find(CompilationResult.class).size() == 0) {
      lastModified = context.getModuleLastModified();
    } else {
      lastModified = System.currentTimeMillis();
    }

    return emitString(logger, selectionScript,
        context.getStrongName()
            + ".nocache.js", lastModified);
  }

  /**
   * The bootstrap script is an analog of the IFrameLinker's selection script,
   * execpt that permutation selection has already been done so it's mostly
   * straight-line code to load the body of the app.
   *
   */
  private String generateBootstrapScript(TreeLogger logger, PermutationContext context, 
  		ArtifactSet artifacts)
      throws UnableToCompleteException {

    // ICK,  copy-and-pasted from SelectionScriptLinker. Ideally work with
    // the GWT folks to make this more easily overridable.

  	
  	// from SelectionScriptLinker.fillSelectionScriptTemplate
  	
    StringBuffer selectionScript;
    try {
      selectionScript = new StringBuffer(
          Utility.getFileFromClassPath(getSelectionScriptTemplate(logger,
              context.getLinkerContext())));
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to read selection script template",
          e);
      throw new UnableToCompleteException();
    }
    
    selectionScript = fillSelectionScriptTemplate(logger, context, artifacts,
        selectionScript);


    return selectionScript.toString();
  }

	private StringBuffer fillSelectionScriptTemplate(TreeLogger logger,
      PermutationContext context, ArtifactSet artifacts,
      StringBuffer selectionScript) throws UnableToCompleteException {
	  String computeScriptBase;
    String processMetas;
    try {
      computeScriptBase = Utility.getFileFromClassPath(COMPUTE_SCRIPT_BASE_JS);
      processMetas = Utility.getFileFromClassPath(PROCESS_METAS_JS);
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to read selection script template",
          e);
      throw new UnableToCompleteException();
    }
    
    replaceAll(selectionScript, "__COMPUTE_SCRIPT_BASE__", computeScriptBase);
    replaceAll(selectionScript, "__PROCESS_METAS__", processMetas);

    
    addPermutationJs(context, selectionScript);
    selectionScript = ResourceInjectionUtil.injectResources(selectionScript, artifacts);

   
    replaceAll(selectionScript, "__MODULE_FUNC__",
        context.getModuleFunctionName());
    replaceAll(selectionScript, "__MODULE_NAME__", context.getModuleName());
    replaceAll(selectionScript, "__HOSTED_FILENAME__", getHostedFilename());
	  return selectionScript;
  }

  // analog to permutationsUtil.addPermutationsJs
	private void addPermutationJs(PermutationContext context,
      StringBuffer selectionScript) {
	  int startPos;
    startPos = selectionScript.indexOf("// __PERMUTATIONS_END__");
    if (startPos != -1) {
      StringBuffer text = new StringBuffer();
      // Just one distinct compilation; no need to evaluate properties
      text.append("strongName = '" + context.getStrongName()  + "';");
      selectionScript.insert(startPos, text);
    }
  }

  @Override
  protected String getSelectionScriptTemplate(TreeLogger logger,
      LinkerContext context) {
    
    if(hostedMode) {
      return super.getSelectionScriptTemplate(logger, context);
    } else {
      return "com/bedatadriven/rebar/appcache/linker/BootstrapTemplate.js";
    }
  }

  /**
   * The permutation map is used on the server side to serve the appropriate permutation
   *
   */
  protected EmittedArtifact emitPermutationMap(TreeLogger logger,
                                               LinkerContext context,
                                               ArtifactSet artifacts)
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

  private EmittedArtifact doEmitManifest(TreeLogger logger,
                                         PermutationContext context,
                                         ManifestWriter writer)
      throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.DEBUG, "Generating " + writer.getSuffix() + " contents",
        null);

    StringBuffer out = readManifestTemplate(logger, context, writer.getSuffix());

    // Generate the manifest entries
    appendEntries(logger, context, writer);



    // use the current time as the version number
    replaceAll(out, "__NAME__", context.getModuleName());
    replaceAll(out, "__VERSION__", context.getStrongName());
    replaceAll(out, "__ENTRIES__", writer.getEntries());

    /*
    * NB: It's tempting to use LinkerContext.optimizeJavaScript here, but the
    * JSON standard requires that the keys in the object literal will be
    * enclosed in double-quotes. In our optimized JS form, the double-quotes
    * would normally be removed.
    */
    return emitBytes(logger, Util.getBytes(out.toString()),
        context.getStrongName() + "." + writer.getSuffix());
  }




  /**
   * Generate a string containing object literals for each manifest entry.
   */
  private void appendEntries(TreeLogger logger,
                             PermutationContext context,
                             ManifestWriter writer)
      throws UnableToCompleteException {

    logger = logger.branch(TreeLogger.DEBUG, "Generating manifest entries",
        null);

    // add the bootstrap script (provided by the server)
    writer.appendEntry(logger, context.getModuleName() + ".nocache.js");

    for (EmittedArtifact artifact : context.getToCache()) {
      if (artifact.getVisibility() == Visibility.Public) {
	
	      
	      logger.log(TreeLogger.DEBUG, "adding to manifest:" + artifact.getPartialPath() + " of class " +
	      		artifact.getClass().getName() + " and visibility " + artifact.getVisibility());
	
	      if (artifact.getPartialPath().endsWith(".gwt.rpc")) {
	        // only used by the server
	        continue;
	      }
	
	      String path = artifact.getPartialPath();
	
	      // certain paths on the Windows platform (notably deferredjs stuff)
	      // show up with backslahes, which throws an illegal escape sequence
	      // error when the json is parsed.
	      path = path.replace('\\', '/');
	
	      logger.log(TreeLogger.DEBUG, "adding: " + path);
	      writer.appendEntry(logger, path);
      } else {
        logger.log(TreeLogger.DEBUG, "excluding " + artifact.getVisibility() + ": " + artifact.getPartialPath());
      }
    }
  }


  private StringBuffer readManifestTemplate(TreeLogger logger, PermutationContext context, String suffix) throws UnableToCompleteException {
    // first try to find a template provided in the module's public
    // folder
    for(PublicResource artifact : context.find(PublicResource.class)) {
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
