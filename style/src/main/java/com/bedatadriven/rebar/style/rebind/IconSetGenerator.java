package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.client.IconSet;
import com.bedatadriven.rebar.style.rebind.icons.*;
import com.bedatadriven.rebar.style.rebind.icons.font.ExternalSvgFontResource;
import com.bedatadriven.rebar.style.rebind.icons.source.GlyphSource;
import com.bedatadriven.rebar.style.rebind.icons.source.IconSource;
import com.bedatadriven.rebar.style.rebind.icons.source.ImageSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static com.google.gwt.core.ext.TreeLogger.Type.*;

/**
 *
 */
public class IconSetGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
                           String typeName) throws UnableToCompleteException {

        JClassType type = context.getTypeOracle().findType(typeName);

        GenerationParameters generationParameters = new GenerationParameterBuilder(context, type).build(logger);
        IconContext iconContext = new IconContext();
        IconStrategy strategy = chooseStrategy(generationParameters);

        IconSetImplWriter writer = new IconSetImplWriter(generationParameters, type, strategy);

        logger.log(INFO, "Generating " + writer.getQualifiedSourceName());

        // if an implementation already exists, we're done.
        if(writer.tryCreate(context, logger)) {

            SourceResolver sourceResolver = new SourceResolver(context, type);
            List<Icon> icons = collectIcons(logger, type, sourceResolver);

            IconArtifacts results = strategy.execute(logger, iconContext, icons);

            emitResources(logger, context, results.getExternalResources());

            writer.writeClassNameMethods(logger, iconContext, icons);
            writer.writeEnsureInjected(results);
            writer.commit(logger);
        }

        return writer.getQualifiedSourceName();
    }


    private void emitResources(TreeLogger parentLogger, GeneratorContext context,
                               List<IconArtifacts.ExternalResource> externalResources) throws UnableToCompleteException {
        if(!externalResources.isEmpty()) {
            TreeLogger logger = parentLogger.branch(TreeLogger.Type.INFO, "Emitting icon set resources");
            for(IconArtifacts.ExternalResource resource : externalResources) {
                logger.log(TreeLogger.Type.INFO, "Emitting " + resource.getName());

                OutputStream outputStream = context.tryCreateResource(logger, resource.getName());
                if(outputStream != null) {
                    try {
                        outputStream.write(resource.getContent());
                    } catch(Exception e) {
                        logger.log(TreeLogger.Type.ERROR, "Error writing resource " + resource.getName());
                        throw new UnableToCompleteException();
                    }
                    context.commitResource(logger, outputStream);
                }
            }
        }
    }

    private IconStrategy chooseStrategy(GenerationParameters params) {
        if(params.getUserAgent().equals(UserAgent.GECKO1_8)) {
            return new SvgBackgroundStrategy();
        } else {
            return new IconFontStrategy(new ExternalSvgFontResource());
        }
    }

    private List<Icon> collectIcons(TreeLogger parentLogger, JClassType interfaceType,
                                       SourceResolver sourceResolver) throws UnableToCompleteException {

        TreeLogger logger = parentLogger.branch(INFO, "Resolving icon sources");

        List<Icon> icons = Lists.newArrayList();
        Map<String, SvgDocument> sourceDocuments = Maps.newHashMap();

        for(JMethod method : AccessorBindings.getClassNameAccessors(interfaceType)) {
            TreeLogger methodLogger = logger.branch(INFO, "Resolving source for " + method.getName() + "()");

            IconSet.Source source = method.getAnnotation(IconSet.Source.class);
            if(source == null) {
                methodLogger.log(ERROR, "Missing the @Source annotation");
                throw new UnableToCompleteException();
            }

            SvgDocument svgDocument = sourceDocuments.get(source.value());
            if(svgDocument == null) {
                String svg = sourceResolver.resolveSourceText(methodLogger, source.value());
                svgDocument = new SvgDocument(svg);
            }

            IconSource iconSource;
            if(source.glyph() == 0) {
                iconSource = new ImageSource(svgDocument);
            } else {
                iconSource = new GlyphSource(svgDocument.getFonts().get(0), source.glyph());
            }

            icons.add(new Icon(method.getName(), iconSource));
        }
        return icons;
    }
}
