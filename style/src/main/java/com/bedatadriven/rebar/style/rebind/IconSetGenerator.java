package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.client.Icon;
import com.bedatadriven.rebar.style.rebind.css.ResourceResolver;
import com.bedatadriven.rebar.style.rebind.icons.IconSource;
import com.bedatadriven.rebar.style.rebind.icons.IconStrategy;
import com.bedatadriven.rebar.style.rebind.icons.InlineSvgStrategy;
import com.google.common.base.Function;
import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

import javax.annotation.Nullable;
import java.io.PrintWriter;

import static com.google.gwt.core.ext.TreeLogger.Type.*;

/**
 *
 */
public class IconSetGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
                           String typeName) throws UnableToCompleteException {

        JClassType type = context.getTypeOracle().findType(typeName);
        String generatedSimpleSourceName = generatedSourceName(logger, context, type);
        String qualifiedSourceName = type.getPackage().getName() + "." + generatedSimpleSourceName;

        logger.log(INFO, "Generating " + qualifiedSourceName);

        PrintWriter pw = context.tryCreate(logger, type.getPackage().getName(), generatedSimpleSourceName);

        // if an implementation already exists, we're done.
        if(pw != null) {

            String css = composeCss(logger, type);

            // write the Java class implementation of the LessResource Interface
            StylesheetImplWriter writer = new StylesheetImplWriter(context, type, generatedSimpleSourceName, pw, css);
            writer.setClassNameMangler(new Function<String, String>() {
                @Nullable
                @Override
                public String apply(String methodName) {
                    return "icon icon-" + methodName;
                }
            });
            writer.write(logger);
        }

        return qualifiedSourceName;
    }

    private String composeCss(TreeLogger parentLogger, JClassType interfaceType) throws UnableToCompleteException {
        TreeLogger logger = parentLogger.branch(INFO, "Composing icon CSS for  " + interfaceType);

        IconStrategy strategy = new InlineSvgStrategy();

        StringBuilder css = new StringBuilder();
        css.append(".icon {");
        strategy.appendCommonDeclarations(css);
        css.append("}");


        for(JMethod method : Methods.getClassNameMethods(interfaceType)) {

            TreeLogger methodLogger = logger.branch(INFO, "Composing icon for " + method);

            IconSource source = loadIcon(methodLogger, method);

            String className = "icon-" + method.getName();
            methodLogger.log(DEBUG, "Class name = " + className);

            css.append(".").append(className).append(" {\n");
            strategy.appendDeclarations(logger, source, css);
            css.append("}").append("\n");
        }
        return css.toString();
    }

    private IconSource loadIcon(TreeLogger logger, JMethod method) throws UnableToCompleteException {
        Icon iconDefinition = method.getAnnotation(Icon.class);
        if(iconDefinition == null) {
            logger.log(ERROR, "Missing the @Icon annotation");
            throw new UnableToCompleteException();
        }

        String source = IconSource.sourceName(iconDefinition);
        String path = ResourceResolver.getPathRelativeToPackage(method.getEnclosingType().getPackage(), source);
        String svg = ResourceResolver.resourceToString(logger, path);
        IconSource iconSource = new IconSource(svg);
        iconSource.parse(logger);
        return iconSource;
    }

    /**
     * We generate a different implementing depending on the browser's support for SVG
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
            logger.log(TreeLogger.Type.ERROR, "Could not get user.agent property", e);
            throw new UnableToCompleteException();
        }
    }
}
