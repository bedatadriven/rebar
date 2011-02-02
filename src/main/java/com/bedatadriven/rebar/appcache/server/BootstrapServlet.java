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

package com.bedatadriven.rebar.appcache.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Serves the appropriate permutation of a GWT file.
 *
 * <code>
 * Requested file: MyModuleName/bootstrap.js
 * Server will look up permutations in
 *
 */
public class BootstrapServlet extends HttpServlet {

  private ServletContext context;
  private Map<String, PropertyProvider> providers;

  private static final Logger logger = Logger.getLogger(BootstrapServlet.class.getName());

  public BootstrapServlet() {
    providers = new HashMap<String, PropertyProvider>();
    registerProvider("user.agent", new UserAgentProvider());
  }

  public final void registerProvider(String propertyName, PropertyProvider provider) {
    providers.put(propertyName, provider);
  }


  @Override
  public void init(ServletConfig config) throws ServletException {
    context = config.getServletContext();
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    // get the non-permuted version of the file
    String[] moduleBaseAndFile = getModuleBase(req);
    String permutation = computePermutation(req, moduleBaseAndFile[0]);

    resp.setHeader("Cache-Control", "no-cache");
    resp.setHeader("Pragma", "no-cache");
    resp.setDateHeader("Expires", new Date().getTime());

    sendFile(resp, moduleBaseAndFile, permutation);
  }

  private void sendFile(HttpServletResponse resp, String[] moduleBaseAndFile, String permutation) throws IOException {
    String path = context.getRealPath(moduleBaseAndFile[0] + permutation + "." + moduleBaseAndFile[1]);
    InputStream is = new FileInputStream(path);
    ServletOutputStream os = resp.getOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;

    while((bytesRead=is.read(buffer))!=-1) {
      os.write(buffer, 0, bytesRead);
    }
  }

  private String[] getModuleBase(HttpServletRequest req) throws ServletException {
    String uri = req.getRequestURI();
    int lastSlash = uri.lastIndexOf('/');
    if(lastSlash < 1) {
      throw new ServletException("Request for resource must be in module path. URI = " + uri);
    }
    return new String[] { uri.substring(0, lastSlash+1), uri.substring(lastSlash+1) };
  }

  private String computePermutation(HttpServletRequest req, String moduleBase) throws ServletException {

    Set<String> matches = new HashSet<String>();

    try {
      InputStreamReader reader =
          new InputStreamReader(
                new FileInputStream(
                  context.getRealPath(moduleBase + "permutations")));

      JsonParser parser = new JsonParser();
      JsonArray permutations = (JsonArray) parser.parse(reader);
      for(int i=0;i!=permutations.size();++i) {
        JsonObject permutation = (JsonObject) permutations.get(i);
        if(matches(req, permutation)) {
          matches.add(permutation.get("permutation").getAsString());
        }
      }
    } catch (FileNotFoundException e) {
      throw new ServletException("Cannot locate permutation map", e);
    }
    if(matches.size() != 1) {
      throw new ServletException("No permutation available");
    }
    return matches.iterator().next();
  }

  private boolean matches(HttpServletRequest request, JsonObject permutation) {
    String strongName = permutation.get("permutation").getAsString();
    JsonObject properties = permutation.getAsJsonObject("properties");
    for(Map.Entry<String, JsonElement> property : properties.entrySet()) {
      PropertyProvider provider = providers.get(property.getKey());
      if(provider != null) {
        String expected = property.getValue().getAsString();
        String actual = provider.get(request);
        if(!expected.equals(actual)) {
          logger.finest("Rejecting " + strongName + ", expected property '" + property.getValue() + "' " +
            "with value '" + expected + "', found '" + actual + "'");
          return false;
        }
      }
    }
    return true;
  }
}
