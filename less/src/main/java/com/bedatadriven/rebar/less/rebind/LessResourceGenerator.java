package com.bedatadriven.rebar.less.rebind;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

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

public class LessResourceGenerator extends Generator {


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
			// this will be combined in the end by the LessLinker
			writeIntermediateCssArtifact(logger, context, type, cssArtifactName(type, generatedSimpleSourceName));

			// write the Java class implementation of the LessResource Interface
			ImplementationWriter writer = new ImplementationWriter(context, type, generatedSimpleSourceName, pw);
			writer.write(logger);
		}

		return qualifiedSourceName;
	}

	private void writeIntermediateCssArtifact(TreeLogger logger,
			GeneratorContext context, JClassType type, String partialArtifactPath) throws UnableToCompleteException {

		String css = compileCSS(logger, context, type);

		OutputStream out = context.tryCreateResource(logger, partialArtifactPath);
		if(out != null) {
			try {
				out.write(css.getBytes(Charsets.UTF_8));
				out.close();
			} catch (IOException e) {
				logger.log(Type.ERROR, "Failed to write intermediate css output");
			}

			GeneratedResource resource = context.commitResource(logger, out);
			resource.setVisibility(Visibility.Private);	
		}
	}

	private String cssArtifactName(JClassType type, String generatedSourceName) {
		return type.getPackage().getName().replace('/',  '.') + generatedSourceName + ".css";
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

		// Gather less sources together
		Preprocessor preprocessor = new Preprocessor();
		preprocessor.preprocess(logger, context, type);

		// Compile LESS -> CSS
		String css = compileLess(logger, preprocessor.asString());

		// Optimize with Closure Stylesheets Compiler
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, css);
		tree.finalizeTree(logger);
		tree.optimize(logger, context);
		tree.emitResources(logger, context);

		return tree.toCompactCSS();
	}

	private String compileLess(TreeLogger parentLogger, String input) throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.INFO, "Compiling LESS...");
		try {
			Function<String, String> lessCompiler = LessCompilerFactory.create();
			String css = lessCompiler.apply(input);
			return css;
		} catch(Exception e) {
			logger.log(Type.ERROR, "Error compiling LESS: " + e.getMessage(), e);
			throw new UnableToCompleteException();
		}
	}
}
