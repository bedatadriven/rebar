package com.bedatadriven.rebar.less.rebind;

import com.bedatadriven.rebar.less.client.Source;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Assembles referenced source files, global imports,
 * and selection properties into a single less import
 */
public class Preprocessor {

	
	private StringBuilder less = new StringBuilder();


	public void preprocess(TreeLogger parentLogger, GeneratorContext context,  JClassType type) throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.DEBUG, "Reading less sources...");
		
		appendSources(type, logger);
		appendGlobalImports(context, logger);
		appendSelectionProperties(context, logger);
	}

	public String asString() {
		return less.toString();
	}

	/**
	 * Append LESS sources referenced in the @Source annotation of the LessResource class
	 */
	private void appendSources(JClassType type, TreeLogger logger) throws UnableToCompleteException {
		
		Source source = type.getAnnotation(Source.class);
		for(String sourceFile : source.value()) {
			less.append(resolveSource(type, logger, sourceFile));
		}
	}
	
	/**
	 * Append LESS sources referenced in gwt.xml module files, in the less.global.import 
	 * configuration-property. These can be used to define or redefine application wide
	 * LESS variables or mixins.
	 */
	private void appendGlobalImports(GeneratorContext context, TreeLogger parentLogger)
			throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.DEBUG, "Reading less.global.imports...");
		ConfigurationProperty globalImports;
		try {
			globalImports = context.getPropertyOracle().getConfigurationProperty("less.global.import");
		} catch (BadPropertyValueException e) {
			logger.branch(Type.ERROR, "Exception getting configuration property 'less.global.import'", e);
			throw new UnableToCompleteException();
		}
		for(String globalImport : globalImports.getValues()) {
			logger.log(Type.DEBUG, "Reading " + globalImport);
			less.append(ResourceResolver.resourceToString(logger, globalImport));
		}
	}
	
	private void appendSelectionProperties(GeneratorContext context, TreeLogger logger) throws UnableToCompleteException {
		SelectionProperty userAgent;
		try {
			userAgent = context.getPropertyOracle().getSelectionProperty(logger, "user.agent");
		} catch (BadPropertyValueException e) {
			logger.log(Type.ERROR, "Exception retrieving selection property 'user.agent'", e);
			throw new UnableToCompleteException();
		}
		less.append("@" + lessPropertyName(userAgent) + ": " + Generator.escape(userAgent.getCurrentValue()) + ";\n");
	}


	private String lessPropertyName(SelectionProperty property) {
		return "gwt-" + property.getName().replace('.', '-');
	}

	private String resolveSource(JClassType type, TreeLogger parentLogger, String sourceFile) throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.DEBUG, "Appending resource '" + sourceFile + "'");
		String path = ResourceResolver.getPathRelativeToPackage(type.getPackage(), sourceFile);
		return ResourceResolver.resourceToString(logger, path);
	}
}
