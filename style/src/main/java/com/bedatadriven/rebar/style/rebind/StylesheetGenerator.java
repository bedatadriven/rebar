package com.bedatadriven.rebar.style.rebind;

import java.net.URL;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.rebind.gss.GssCompiler;
import com.bedatadriven.rebar.style.rebind.gss.GssTree;
import com.bedatadriven.rebar.style.rebind.less.LessCompiler;
import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

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

        // Collect all the details about how this stylesheet is
        // to be generated
        GenerationParameters generationParameters = new GenerationParameterBuilder(context, type).build(logger);

        // Our goal here is to write the source of a Java class which implement our
        // stylesheet interface
        StylesheetImplWriter generatedSource = new StylesheetImplWriter(generationParameters, type);

		logger.log(Type.INFO, "Generating " + generatedSource.getQualifiedSourceName());

		// if an implementation already exists, we're done.
		if(generatedSource.tryCreate(context, logger)) {

            SourceResolver sourceResolver = new SourceResolver(context, type);

			// compile the LESS to CSS and write it out to an intermediate artifact
            URL sourceUrl = locateSource(logger, type, sourceResolver);
            String css = new LessCompiler(logger).compile(sourceUrl);
			GssTree tree = new GssCompiler().compile(logger, css);

            // build the class and write
            AccessorBindings accessorBindings = AccessorBindings.build(generationParameters, logger, type, tree);
            StylesheetImpl impl = new StylesheetImpl(generationParameters, tree, accessorBindings);
            impl.build(logger);
            impl.write(generatedSource);

            generatedSource.commit(logger);
		}

		return generatedSource.getQualifiedSourceName();
	}


    private URL locateSource(TreeLogger logger, JClassType type, SourceResolver sourceResolver)
            throws UnableToCompleteException {

        String sourceName = sourceNameFromInterface(type);

        URL resourceUrl = sourceResolver.tryResolveURL(sourceName);
        if(resourceUrl == null) {
            logger.log(TreeLogger.Type.ERROR, "Cannot find LESS source for " + type.getName() + ". Either annotate the type" +
                    " with a @Source attribute, or use the convention of {Component}Stylesheet => {Component}.less");

            throw new UnableToCompleteException();
        }
        return resourceUrl;
    }

    private String sourceNameFromInterface(JClassType type) {
        Source source = type.getAnnotation(Source.class);
        if(source != null) {
            return source.value();
        } else {
            String typeName = type.getSimpleSourceName();
            if(typeName.endsWith("Stylesheet")) {
                return typeName.substring(0, typeName.length() - "Stylesheet".length());
            } else {
                return typeName;
            }
        }
    }



}
