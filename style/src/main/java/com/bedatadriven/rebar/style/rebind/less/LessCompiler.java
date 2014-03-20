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
package com.bedatadriven.rebar.style.rebind.less;

import com.google.common.base.Function;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.net.URL;


public class LessCompiler {

    private TreeLogger logger;

    public LessCompiler(TreeLogger parentLogger) {
        logger = parentLogger.branch(TreeLogger.Type.INFO, "Compiling LESS...");

    }

    public String compile(URL sourceURL) throws UnableToCompleteException {

        // TODO: this won't work if the .less file is in a jar...
        LessCompilerContext context = new LessCompilerContext(logger, sourceURL.getFile());

        try {
            Function<LessCompilerContext, String> compiler = LessCompiler.newCompiler();
            return compiler.apply(context);
        } catch(Exception e) {
            logger.log(TreeLogger.Type.ERROR, "Error compiling LESS: " + e.getMessage(), e);
            throw new UnableToCompleteException();
        }
    }

    /**
	 * Constructs a new <code>LessCompiler</code>.
	 */
	@SuppressWarnings("unchecked")
    public static Function<LessCompilerContext, String> newCompiler() {
		try { 
			Class compiledClass = Class.forName("com.bedatadriven.rebar.less.rebind.LessImpl");
			return (Function<LessCompilerContext, String>) compiledClass.newInstance();
		} catch(Exception e) {
			throw new RuntimeException("Exception loading LESS compiler", e); 
		}
	}
}
