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

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;

import java.util.*;

class PermutationContext {
  private LinkerContext linkerContext;
  private ArtifactSet resources;
  private CompilationResult compilation;
  private List<EmittedArtifact> toCache;

  public PermutationContext(LinkerContext linkerContext, ArtifactSet resources, CompilationResult compilation) {
    this.linkerContext = linkerContext;
    this.resources = resources;
    this.compilation = compilation;
    this.toCache = new ArrayList<EmittedArtifact>();
  }

  private String generateTimestampVersion() {
    return Long.toString(new Date().getTime());
  }

  public void addToCache(Collection<EmittedArtifact> artifacts) {
    toCache.addAll(artifacts);
  }

  public LinkerContext getLinkerContext() {
    return linkerContext;
  }

  public String getModuleFunctionName() {
    return linkerContext.getModuleFunctionName();
  }

  public String getModuleName() {
    return linkerContext.getModuleName();
  }

  public <X extends Artifact> SortedSet<X> find(Class<X> stylesheetReferenceClass) {
    return resources.find(stylesheetReferenceClass);
  }

  public long getModuleLastModified() {
    return linkerContext.getModuleLastModified();
  }

  public String getStrongName() {
    return compilation.getStrongName();
  }

  public String optimizeJavaScript(TreeLogger logger, String selectionScript) throws UnableToCompleteException {
    return linkerContext.optimizeJavaScript(logger, selectionScript);
  }

  public List<EmittedArtifact> getToCache() {
    return toCache;
  }
}
