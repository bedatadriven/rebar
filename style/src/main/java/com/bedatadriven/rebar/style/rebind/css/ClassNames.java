package com.bedatadriven.rebar.style.rebind.css;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

/**
 * Functions related to CSS Class names
 */
public class ClassNames {


    /**
     * Converts a camelCaseName or to a hyphenated-name
     */
    public static String hyphenate(String method) {

        assert method != null && method.length() >= 1;

        StringBuilder out = new StringBuilder();
        out.append(method.charAt(0));

        for(int i=1;i<method.length();++i) {

            if(method.charAt(i) == '_') {

                out.append('-');

            } else {
                if(isWordBoundaryAt(method, i)) {
                    out.append('-');
                }
                out.append(method.charAt(i));
            }
        }
        return out.toString().toLowerCase();
    }

    /**
     * Validates that the class names used in the style sheet follow the lower-case-dash convention.
     *
     */
    public static void validateClassNameStyles(
            TreeLogger parentLogger,
            TreeLogger.Type logLevel,
            Iterable<String> classNames)
            throws UnableToCompleteException {

        TreeLogger logger = parentLogger.branch(logLevel, "Checking naming conventions of CSS class names");

        boolean violations = false;
        for(String className : classNames) {
            if(isCamelCase(className)) {
                logger.log(logLevel, "CSS Class name ." + className + " uses camel case. Prefer lower " +
                        "case hyphenated: " + hyphenate(className));
                violations = true;
            }
            if(usesUnderscores(className)) {
                logger.log(logLevel, "CSS Class name ." + className + " uses underscores Prefer hyphens: "
                        + hyphenate(className));
                violations = true;
            }
        }

        if(violations && logLevel == TreeLogger.Type.ERROR) {
            throw new UnableToCompleteException();
        }
    }


    private static boolean isWordBoundaryAt(String method, int i) {
        return isLowerCase(method.charAt(i - 1)) &&
                isUpperCase(method.charAt(i));
    }

    public static boolean isCamelCase(String className) {
        return className.toLowerCase().equals(className);
    }

    public static boolean usesUnderscores(String className) {
        return className.indexOf('_') != -1;
    }
}
