package com.bedatadriven.rebar.style.rebind;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.rebind.css.*;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.GeneratedResource;
import com.google.gwt.core.ext.typeinfo.JClassType;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

/**
 * Generates the implementation of a {@code Stylesheet} interface and compiles
 * its LESS sources to CSS.
 * 
 * <p>The results at this stage are emitted as private artifacts that are 
 * concatenated and further optimized at the linker stage.
 */
public class StylesheetGenerator extends Generator {


	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {

		JClassType type = context.getTypeOracle().findType(typeName);
		String generatedSimpleSourceName = generatedSourceName(logger, context, type);
		String qualifiedSourceName = type.getPackage().getName() + "." + generatedSimpleSourceName;

		logger.log(Type.INFO, "Generating " + qualifiedSourceName);
		
		PrintWriter pw = context.tryCreate(logger, type.getPackage().getName(), generatedSimpleSourceName);

		// if an implementation already exists, we're done.
		if(pw != null) {

			// compile the LESS to CSS and write it out to an intermediate artifact
			String css = compileCSS(logger, context, type);
			
			// write the Java class implementation of the LessResource Interface
			StylesheetImplWriter writer = new StylesheetImplWriter(context, type, generatedSimpleSourceName, pw, css);
            writer.write(logger);
		}

		return qualifiedSourceName;
	}

	/**
	 * This must be distinct for different user agents.
	 * @param logger
	 * @param context
	 * @param type
	 * @return
	 * @throws UnableToCompleteException
	 */
	private String generatedSourceName(TreeLogger logger,
			GeneratorContext context, JClassType type)
					throws UnableToCompleteException {
		return type.getSimpleSourceName() + "Impl_" + getUserAgent(logger, context);
	}

	private String getUserAgent(TreeLogger logger, GeneratorContext context)
			throws UnableToCompleteException {
		try {
			return context.getPropertyOracle().getSelectionProperty(logger, "user.agent").getCurrentValue();
		} catch (BadPropertyValueException e) {
			logger.log(Type.ERROR, "Could not get user.agent property", e);
			throw new UnableToCompleteException();
		}
	}

	private String compileCSS(TreeLogger logger, GeneratorContext context, JClassType type) throws UnableToCompleteException {

        Source source = type.getAnnotation(Source.class);
        String absolutePath = ResourceResolver.getPathRelativeToPackage(type.getPackage(), source.value());
        URL resourceUrl = ResourceResolver.getResourceUrl(logger, absolutePath);

        // TODO: this won't work if the .less file is in a jar...
        LessCompilerContext lessContext = new LessCompilerContext(logger, resourceUrl.getFile());

        // Compile LESS -> CSS
		String css = compileLess(logger, lessContext);

		// Optimize with Closure Stylesheets Compiler
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, css);
		tree.finalizeTree(logger);
		tree.optimize(logger, context);
		tree.emitResources(logger, context);

		return tree.toCompactCSS();
	}

	private String compileLess(TreeLogger parentLogger, LessCompilerContext lessContext) throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.INFO, "Compiling LESS...");
		try {
			Function<LessCompilerContext, String> lessCompiler = LessCompilerFactory.create();
            return lessCompiler.apply(lessContext);
        } catch(Exception e) {
			logger.log(Type.ERROR, "Error compiling LESS: " + e.getMessage(), e);
			throw new UnableToCompleteException();
		}
	}
}
