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

import com.bedatadriven.rebar.appcache.client.Html5AppCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Serves the appropriate permutation of a loader script or application manifest.
 * <p/>
 * <code>
 * Requested file: MyModuleName/bootstrap.js
 * Server will look up permutations in
 */
public class DefaultSelectionServlet extends HttpServlet {

  public static final int CACHE_OBSOLETE = 404;
  
  private static final Logger logger = Logger.getLogger(DefaultSelectionServlet.class.getName());
  
  private final Map<String, PropertyProvider> providers;

  public DefaultSelectionServlet() {
    providers = new HashMap<>();
    registerProvider("user.agent", new UserAgentProvider());
  }

  public final void registerProvider(String propertyName, PropertyProvider provider) {
    providers.put(propertyName, provider);
  }
  
  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    // get the non-permuted version of the file
    Path path = new Path(req.getRequestURI());
    
    logger.info("path.locale = " + path.locale);
    logger.info("path.fileType = " + path.fileType);

    // special hook to help remove application cache
    if (path.isManifest() && isAppCacheDisabled(req)) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // read the permutation map that was prepared during the linker phase
    JsonArray permutationMap = readPermutationMap(path);

    if (permutationMap == null) {
      serveDevModeResource(resp, path);
    } else {
      try {
        String permutation = computePermutation(req, path, permutationMap);
        if (permutation == null) {
          handleNoAvailablePermutation(path, resp);
        } else {
          servePermutationSpecificFile(path, permutation, resp);
        }
      } catch (Exception e) {
        handleSelectionException(path, e, resp);
      }
    }
  }

  private boolean isAppCacheDisabled(HttpServletRequest req) {
    if (req.getCookies() != null) {
      for (Cookie cookie : req.getCookies()) {
        if (cookie.getName().equals(Html5AppCache.DISABLE_COOKIE_NAME) &&
            cookie.getValue().equals(Html5AppCache.DISABLE_COOKIE_VALUE)) {
          return true;
        }
      }
    }
    return false;
  }

  private void serveDevModeResource(HttpServletResponse resp, Path resource) throws IOException {
    throw new UnsupportedOperationException();
  }

  private void servePermutationSpecificFile(Path path, String permutation, HttpServletResponse resp) throws IOException, ServletException {
    // first verify that the file exists and is readable
    String resource = path.forPermutation(permutation);
    
    logger.info("Resolved path to: " + resource);
    
    if (!new File(getServletContext().getRealPath(resource)).exists()) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } else {
      resp.setDateHeader("Expires", new Date().getTime());
      resp.setContentType(path.getContentType());

      serve(resp, resource);
    }
  }

  private void serve(HttpServletResponse resp, String path) throws IOException {
    InputStream is = new FileInputStream(getServletContext().getRealPath(path));
    OutputStream os = resp.getOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;

    while ((bytesRead = is.read(buffer)) != -1) {
      os.write(buffer, 0, bytesRead);
    }
  }

  private JsonArray readPermutationMap(Path path) {
    try {
      InputStreamReader reader =
          new InputStreamReader(
              new FileInputStream(
                  getServletContext().getRealPath(path.moduleBase + "permutations")));

      JsonParser parser = new JsonParser();
      return (JsonArray) parser.parse(reader);

    } catch (FileNotFoundException e) {
      logger.info("No permutations map found, (we are probably in dev mode) will return default selection script");
      return null;
    }
  }

  private String computePermutation(HttpServletRequest req, Path path, JsonArray permutationMap) throws ServletException {

    Map<String, String> properties = computeProperties(req);
    properties.put("locale", path.locale);
    
    Set<String> matches = new HashSet<>();

    for (int i = 0; i != permutationMap.size(); ++i) {
      JsonObject permutation = (JsonObject) permutationMap.get(i);
      if (matches(properties, permutation)) {
        matches.add(permutation.get("permutation").getAsString());
      }
    }

    if (matches.size() == 1) {
      return matches.iterator().next();
    } else {
      return null;
    }
  }

  private Map<String, String> computeProperties(HttpServletRequest req) {
    Map<String, String> properties = new HashMap<>();
    for (Entry<String, PropertyProvider> entry : providers.entrySet()) {
      properties.put(entry.getKey(), entry.getValue().get(req));
    }
    return properties;
  }

  private boolean matches(Map<String, String> properties, JsonObject permutation) {
    String strongName = permutation.get("permutation").getAsString();
    JsonObject permProperties = permutation.getAsJsonObject("properties");
    for (Map.Entry<String, JsonElement> property : permProperties.entrySet()) {
      String expected = property.getValue().getAsString();
      String actual = properties.get(property.getKey());
      if (actual != null && !expected.equals(actual)) {
        logger.finest("Rejecting " + strongName + ", expected property '" + property.getValue() + "' " +
            "with value '" + expected + "', found '" + actual + "'");
        return false;
      }
    }
    return true;
  }

  /**
   * Handles the case in which an exception was thrown while trying to compute
   * properties for the selection of the permutation.
   *
   */
  protected void handleSelectionException(Path path, Exception e, HttpServletResponse resp) throws IOException {
    sendErrorMessage(path, "Error selecting permutation: " + e.getMessage(), resp);
  }

  protected void handleNoAvailablePermutation(Path path,  HttpServletResponse resp) throws IOException {
    sendErrorMessage(path, "Your browser is unsupported", resp);
  }

  protected final void sendErrorMessage(Path path, String message, HttpServletResponse resp) throws IOException {
    if (path.fileType.equals("js")) {
      resp.setContentType("application/javascript");
      resp.getWriter().println("window.alert('" + message.replace("'", "\'") + "');");
    } else {
      resp.sendError(CACHE_OBSOLETE, message);
    }
  }


  protected final static class Path {

    public final String moduleBase;
    public final String locale;
    public final String fileType;

    public Path(String uri) throws ServletException {
      int lastSlash = uri.lastIndexOf('/');
      if (lastSlash < 1) {
        throw new ServletException("Request for resource must be in module path. URI = " + uri);
      }
      
      this.moduleBase = uri.substring(0, lastSlash + 1);
      
      String file = uri.substring(lastSlash + 1);
      String[] fileParts = file.split("\\.");
      
      if(fileParts.length != 2) {
        throw new ServletException("Invalid file name: expected {locale}.{js|appcache}");
      }
      
      this.locale = fileParts[0];
      this.fileType = fileParts[1];
    }
    
    public String forPermutation(String strongName) {
      return moduleBase + strongName + "." + fileType;
    }

    public String getContentType() {
      if(fileType.equals("js")) {
        return "application/javascript";
      } else if(fileType.equals("appcache")) {
        return "text/cache-manifest";
      } else {
        return "application/octet";
      }
    }
    
    public boolean isManifest() {
      return fileType.equals("appcache");
    }
  }
}
