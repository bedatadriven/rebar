/* Copyright 2011-2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bedatadriven.rebar.less.rebind;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;

/**
 * The LESS compiler to compile LESS sources to CSS stylesheets.
 * <p>
 * The compiler uses Rhino (JavaScript implementation written in Java), Envjs 
 * (simulated browser environment written in JavaScript), and the official LESS 
 * JavaScript compiler.<br />
 * Note that the compiler is not a Java implementation of LESS itself, but rather 
 * integrates the LESS JavaScript compiler within a Java/JavaScript browser 
 * environment provided by Rhino and Envjs.
 * </p>
 * <p>
 * The compiler comes bundled with the Envjs and LESS JavaScript, so there is 
 * no need to include them yourself. But if needed they can be overridden.
 * </p>
 * <h4>Basic code example:</h4>
 * <pre>
 * LessCompiler lessCompiler = new LessCompiler();
 * String css = lessCompiler.compile("@color: #4D926F; #header { color: @color; }");
 * </pre>
 * 
 * @author Marcel Overdijk
 * @see <a href="http://lesscss.org/">LESS - The Dynamic Stylesheet language</a>
 * @see <a href="http://www.mozilla.org/rhino/">Rhino - JavaScript for Java</a>
 * @see <a href="http://www.envjs.com/">Envjs - Bringing the Browser</a>
 */
public class LessCompiler {

   
   
    private String encoding = null;
    
    private Function compileFunction;
    
    private Scriptable scope;
    
    /**
     * Constructs a new <code>LessCompiler</code>.
     */
    public LessCompiler() {
    }
    
    
    
    /**
     * Returns the character encoding used by the compiler when writing the output <code>File</code>.
     * 
     * @return The character encoding used by the compiler when writing the output <code>File</code>.
     */
    public String getEncoding() {
        return encoding;
    }
    
    /**
     * Sets the character encoding used by the compiler when writing the output <code>File</code>.
     * If not set the platform default will be used.
     * Must be set before {@link #init()} is called.
     * 
     * @param The character encoding used by the compiler when writing the output <code>File</code>.
     */
    public synchronized void setEncoding(String encoding) {
        if (scope != null) {
            throw new IllegalStateException("This method can only be called before init()");
        }
        this.encoding = encoding;
    }
    
    /**
     * Initializes this <code>LessCompiler</code>.
     * <p>
     * It is not needed to call this method manually, as it is called implicitly by the compile methods if needed.
     * </p>
     * @throws UnableToCompleteException 
     */
    public synchronized void init(TreeLogger logger) throws UnableToCompleteException {
    	TreeLogger initLogger = logger.branch(Type.INFO, "Initializing less compiler");
        long start = System.currentTimeMillis();

        try {
	        Context cx = Context.enter();
	        cx.setOptimizationLevel(-1); 
	        cx.setLanguageVersion(Context.VERSION_1_7);
	        
	        Global global = new Global(); 
	        global.init(cx); 
	        
	        scope = cx.initStandardObjects(global);
	        
	        List<URL> jsUrls = Lists.newArrayList();
	        jsUrls.add(getClass().getResource("env.rhino.js"));
	        jsUrls.add(getClass().getResource("less.js"));
	        jsUrls.add(getClass().getResource("preprocess.js"));
	       
	        for(URL url : jsUrls){
		        InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
		        try {
		        	cx.evaluateReader(scope, inputStreamReader, url.toString(), 1, null);
		        } finally{
		        	inputStreamReader.close();
		        }
	        }
            compileFunction = (Function)scope.get("compile", scope);
        }
        catch (Exception e) {
            initLogger.log(Type.ERROR, "Failed to initialize LESS compiler", e);
            throw new UnableToCompleteException();
            
        } finally{
        	Context.exit();
        }
        
        initLogger.log(Type.DEBUG, "Finished initialization of LESS compiler in " + (System.currentTimeMillis() - start) + " ms.");
    }
    
    /**
     * Compiles the LESS input <code>String</code> to CSS. 
     * 
     * @param input The LESS input <code>String</code> to compile. 
     * @return The CSS.
     */
    public String compile(TreeLogger logger, String input) throws LessException {
    	
    	Stopwatch stopwatch = Stopwatch.createStarted();
    	logger.log(Type.DEBUG, "Starting compilation of LESS source");
    	
        try {
        	Context cx = Context.enter();
            Object result = compileFunction.call(cx, scope, null, new Object[]{input });

            logger.log(Type.DEBUG, "Finished compilation of LESS source in " + stopwatch);
          
            return result.toString();
        }
        catch (Exception e) {
            if (e instanceof JavaScriptException) {
                Scriptable value = (Scriptable)((JavaScriptException)e).getValue();
                if (value != null && ScriptableObject.hasProperty(value, "message")) {
                    String message = ScriptableObject.getProperty(value, "message").toString();
                    throw new LessException(message, e);
                }
            }
            throw new LessException(e);
        } finally{
        	Context.exit();
        }
    }
    
}
