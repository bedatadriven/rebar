package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.rebind.icons.*;
import com.bedatadriven.rebar.style.rebind.icons.font.ExternalSvgFontResource;
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

        StylesheetImplWriter writer = new StylesheetImplWriter(generationParameters, type);

        logger.log(INFO, "Generating " + writer.getQualifiedSourceName());

        // if an implementation already exists, we're done.
        if(writer.tryCreate(context, logger)) {

            SourceResolver sourceResolver = new SourceResolver(context, type);
            List<Icon> icons = collectIcons(logger, type, sourceResolver);

            IconStrategy strategy = chooseStrategy(generationParameters);
            IconArtifacts results = strategy.execute(logger, new IconContext(), icons);

            emitResources(logger, context, results.getExternalResources());

            writer.writeGetName();
            writer.writeGetText(results.getStylesheet());
            writer.writeClassNameMethods(accessorMap(icons));
            writer.writeEnsureInjected();
            writer.commit(logger);
        }

        return writer.getQualifiedSourceName();
    }

    private Map<String, String> accessorMap(List<Icon> icons) {
        Map<String, String> map = Maps.newHashMap();
        for(Icon icon : icons) {
            map.put(icon.getAccessorName(), icon.getClassName());
        }
        return map;
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
        for(JMethod method : AccessorBindings.getClassNameAccessors(interfaceType)) {

            TreeLogger methodLogger = logger.branch(INFO, "Resolving source for " + method.getName() + "()");
            String svg = sourceResolver.resolveSourceText(methodLogger, method);
            SvgDocument icon = new SvgDocument(svg);

            icons.add(new Icon(method.getName(), new ImageSource(icon)));
        }
        return icons;
    }
}
