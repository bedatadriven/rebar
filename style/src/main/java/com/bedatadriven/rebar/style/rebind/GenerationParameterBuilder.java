package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.client.Strictness;
import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.JClassType;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Collects parameters affecting stylesheet generation from type information and the generation context
 */
public class GenerationParameterBuilder {

    public static final String NAMING_CONVENTION_ENFORCEMENT_LEVEL = "rebar.style.convention.enforcement.level";

    private final GeneratorContext context;
    private final JClassType interfaceType;
    private TreeLogger logger;

    public GenerationParameterBuilder(GeneratorContext context, JClassType interfaceType) {
        this.context = context;
        this.interfaceType = interfaceType;
    }

    public GenerationParameters build(TreeLogger logger)
            throws UnableToCompleteException {

        this.logger = logger;

        GenerationParameters parameters = new GenerationParameters();
        parameters.setUserAgent(getUserAgent());
        parameters.setNamingConventionEnforcementLevel(parseEnforcementLevel(NAMING_CONVENTION_ENFORCEMENT_LEVEL));

        Strictness strictness = interfaceType.getAnnotation(Strictness.class);
        if(strictness != null) {
            parameters.setRequireAccessorsForAllClasses(strictness.requireAccessorsForAllClasses());
            parameters.setIgnoreMissingClasses(strictness.ignoreMissingClasses());
        }

        return parameters;
    }

    private boolean isPresent(Class<? extends Annotation> annotation) {
        return interfaceType.getAnnotation(annotation) != null;
    }


    private UserAgent getUserAgent() throws UnableToCompleteException {
        String value;
        try {
            value = context.getPropertyOracle().getSelectionProperty(logger, "user.agent").getCurrentValue();
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.Type.ERROR, "Could not get user.agent property", e);
            throw new UnableToCompleteException();
        }
        try {
            return UserAgent.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.log(TreeLogger.Type.ERROR, "Unexpected user.agent property, expected one of [ " +
                    Arrays.toString(UserAgent.values()) + "]" );
            throw new UnableToCompleteException();
        }
    }


    private TreeLogger.Type parseEnforcementLevel(String propertyName)
            throws UnableToCompleteException {
        ConfigurationProperty property = null;
        try {
            property = context.getPropertyOracle().getConfigurationProperty(propertyName);
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.Type.ERROR, "Our configuration property " + propertyName +
                    " is not defined, something has gone wrong!");
            throw new UnableToCompleteException();
        }

        if( property == null ||
            property.getValues().isEmpty() ||
            property.getValues().get(0) == null) {

            return TreeLogger.Type.DEBUG;
        }
        try {
            return TreeLogger.Type.valueOf(property.getValues().get(0));
        } catch(IllegalArgumentException e) {
            logger.log(TreeLogger.Type.ERROR, "Invalid property value for " + propertyName + ": expected [ " +
                    TreeLogger.Type.values() + "]");
            throw new UnsupportedOperationException();
        }
    }

}
