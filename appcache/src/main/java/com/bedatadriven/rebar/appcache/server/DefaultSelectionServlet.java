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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Serves the appropriate permutation of a loader script or application manifest.
 *
 * <code>
 * Requested file: MyModuleName/bootstrap.js
 * Server will look up permutations in
 *
 */
public class DefaultSelectionServlet extends HttpServlet {

  private ServletContext context;
  private Map<String, PropertyProvider> providers;
  

  private static final Logger logger = Logger.getLogger(DefaultSelectionServlet.class.getName());

  public DefaultSelectionServlet() {
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
    Path path = getModuleBase(req);
    
    // read the permutation map that was prepared during the linker phase
    JsonArray permutationMap = readPermutationMap(path);
    
    if(permutationMap == null) {
    	serveDefault(resp, path);
    } else {
    	try {
    		String permutation = computePermutation(req, permutationMap, path);	     
    		if(permutation == null) {
    			handleNoAvailablePermutation(path, resp);
    		} else {
    			servePermutationSpecificFile(path, permutation, resp);
    		}
	    } catch(Exception e) {
	    	handleSelectionException(path, e, resp);
	    }
    }
  }

	private void serveDefault(HttpServletResponse resp, Path resource) throws IOException {
  	serve( resp, resource.moduleBase + resource.file );
  }

  private void servePermutationSpecificFile(Path path, String permutation, HttpServletResponse resp) throws IOException, ServletException {    
    // first verify that the file exists and is readable
  	String resource = resolvePermutationSpecificResource(path, permutation);
  	if(!new File(context.getRealPath(resource)).exists()) {
  		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  	} else {
  		
    	resp.setHeader("Cache-Control", "no-cache");
      resp.setHeader("Pragma", "no-cache");
      resp.setDateHeader("Expires", new Date().getTime());

      if(path.file.endsWith(".appcache")) {
        resp.setContentType("text/cache-manifest");

      } else if(path.file.endsWith(".js")) {
        resp.setContentType("application/javascript");
      }
      
      serve(resp, resource);
  	} 
  }
  
  protected String resolvePermutationSpecificResource(Path path, String permutation) throws ServletException {
  	if(path.file.startsWith(path.moduleName + ".")) {
  		return path.moduleBase + permutation + path.file.substring(path.moduleName.length());
  	} else {
  		logger.severe("ScriptSelectionServlet does not know how to serve '" + path.file + ", expected '" + 
  				path.moduleName + ".xxxx'");
  		throw new ServletException();
  	}
  }
  
  private void serve(HttpServletResponse resp, String path) throws IOException {
    InputStream is = new FileInputStream(context.getRealPath(path));
    ServletOutputStream os = resp.getOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;

    while((bytesRead=is.read(buffer))!=-1) {
      os.write(buffer, 0, bytesRead);
    }
  }

  private Path getModuleBase(HttpServletRequest req) throws ServletException {
    String uri = req.getRequestURI();
    int lastSlash = uri.lastIndexOf('/');
    if(lastSlash < 1) {
      throw new ServletException("Request for resource must be in module path. URI = " + uri);
    }
    String file = uri.substring(lastSlash+1);
    String path = uri.substring(0, lastSlash+1);
    
    lastSlash = path.lastIndexOf('/',path.length()-2);
    String module = path.substring(lastSlash+1, path.length()-1);
    
    return new Path(path, file, module);
  }
  
  private JsonArray readPermutationMap(Path path) {
    try {
      InputStreamReader reader =
          new InputStreamReader(
                new FileInputStream(
                  context.getRealPath(path.moduleBase + "permutations")));

      JsonParser parser = new JsonParser();
      return (JsonArray) parser.parse(reader);

    } catch (FileNotFoundException e) {
      logger.info("No permutations map found, (we are probably in dev mode) will return default selection script");
      return null;	
    }
  }

  private String computePermutation(HttpServletRequest req, JsonArray permutationMap, Path path) throws ServletException {

  	Map<String, String> properties = computeProperties(req);
  	Set<String> matches = new HashSet<String>();
  	
  	for(int i=0;i!=permutationMap.size();++i) {
  		JsonObject permutation = (JsonObject) permutationMap.get(i);
  		if(matches(properties, permutation)) {
  			matches.add(permutation.get("permutation").getAsString());
  		}
  	}

  	if(matches.size() == 1) {
  		return matches.iterator().next();    	
  	} else {
  		return null;
  	}
  }

	private Map<String, String> computeProperties(HttpServletRequest req) {
	  Map<String, String> properties = new HashMap<String, String>();
	  for(Entry<String, PropertyProvider> entry : providers.entrySet()) {
	  	properties.put(entry.getKey(), entry.getValue().get(req));
	  }
	  return properties;
	}

	private boolean matches(Map<String, String> properties, JsonObject permutation) {
    String strongName = permutation.get("permutation").getAsString();
    JsonObject permProperties = permutation.getAsJsonObject("properties");
    for(Map.Entry<String, JsonElement> property : permProperties.entrySet()) {
      String expected = property.getValue().getAsString();
      String actual = properties.get(property.getKey());
      if(actual != null && !expected.equals(actual)) {
        logger.finest("Rejecting " + strongName + ", expected property '" + property.getValue() + "' " +
          "with value '" + expected + "', found '" + actual + "'");
        return false;
      }
    }
    return true;
  }
  
	/**
	 * Handles the case in which an exception was thrown during 
	 * 
	 * @param path
	 * @param e
	 * @throws IOException 
	 */
	protected void handleSelectionException(Path path, Exception e, HttpServletResponse resp) throws IOException {
		sendErrorMessage(path, "Error selecting permutation: " + e.getMessage(), resp);
  }
	
  protected void handleNoAvailablePermutation(Path path,
      HttpServletResponse resp) throws IOException {
	  sendErrorMessage(path, "Your browser is unsupported", resp);
  }

  protected final void sendErrorMessage(Path path, String message, HttpServletResponse resp) throws IOException {
  	if(path.file.endsWith(".js")) {
  		resp.setContentType("application/javascript");
  		resp.getWriter().println("window.alert('" + message.replace("'", "\'") + "');");
  	} else {
  		resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, message);
  	}
  }
  
  
  protected final static class Path {
    
    public Path(String path, String file, String moduleName) {
      super();
      this.moduleBase = path;
      this.file = file;
      this.moduleName = moduleName;
    }
    public final String moduleBase;
    public final String file;
    public final String moduleName;
    
    public boolean isSelectionScript() {
      return file.equals(moduleName + ".nocache.js");
    }
  }
}
