package com.bedatadriven.rebar.style.rebind;

import com.bedatadriven.rebar.style.rebind.css.ClassNames;
import com.bedatadriven.rebar.style.rebind.gss.GssTree;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.resources.client.CssResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps accessors on the Stylesheet interface to CSS Class Names
 */
public class AccessorBindings {

    private GenerationParameters params;
    private List<JMethod> accessors;
    private Set<String> classNames;
    private Map<String, String> accessorToClassName = Maps.newHashMap();


    public static AccessorBindings build(GenerationParameters params, TreeLogger logger,
                                         JClassType interfaceType, GssTree tree) throws UnableToCompleteException {
        List<JMethod> accessors = getClassNameAccessors(interfaceType);
        Set<String> classNames = tree.getClassNames();
        return new AccessorBindings(params, accessors, classNames).build(logger);
    }

    public AccessorBindings(GenerationParameters params, List<JMethod> accessors,
                            Set<String> classNames) {
        this.params = params;
        this.accessors = accessors;
        this.classNames = classNames;
    }

    public static List<JMethod> getClassNameAccessors(JClassType interfaceType) {

        Set<String> toSkip = Sets.newHashSet("getName", "getText");

        List<JMethod> methods = Lists.newArrayList();
        for(JMethod method : interfaceType.getOverridableMethods()) {

            // Skip the methods defined on CSS Resource
            if(toSkip.contains(method.getName())) {
                continue;
            }
            if(isReturnTypeString(method.getReturnType().isClass()) && method.getParameters().length == 0 &&
                    !method.getEnclosingType().getName().equals(CssResource.class.getSimpleName())) {
                methods.add(method);
            }
        }
        return methods;
    }

    private static boolean isReturnTypeString(JClassType classReturnType) {
		return (classReturnType != null
				&& String.class.getName().equals(classReturnType.getQualifiedSourceName()));
	}

    public AccessorBindings build(TreeLogger parentLogger)
            throws UnableToCompleteException {

        TreeLogger logger = parentLogger.branch(TreeLogger.Type.DEBUG, "Matching accessors to class names");

        boolean hasMissing = false;

        for(JMethod accessor : accessors) {
            String className = matchClassName(logger, accessor, classNames);
            if(className == null) {
                hasMissing = true;
                accessorToClassName.put(accessor.getName(), accessor.getName());

            } else {
                accessorToClassName.put(accessor.getName(), className);
            }
        }
        if(hasMissing && !params.isIgnoreMissingClasses()) {
            throw new UnableToCompleteException();
        }
        return this;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(accessorToClassName);
    }

    private String matchClassName(TreeLogger logger, JMethod accessor, Set<String> classNames)
            throws UnableToCompleteException {

        String methodLabel = accessor.getName() + "()";

        CssResource.ClassName nameAnnotation = accessor.getAnnotation(CssResource.ClassName.class);
        if(nameAnnotation != null) {
            String name = nameAnnotation.value();
            if(classNames.contains(name)) {
                return name;
            } else {
                logger.log(TreeLogger.Type.ERROR, "Method " + methodLabel + " annotated with @ClassName '" + name +
                        "' but no matching" +
                        "class name in stylesheet source.");
                throw new UnableToCompleteException();
            }
        }

        Set<String> candidates = Sets.newHashSet();
        candidates.add(accessor.getName());
        candidates.add(ClassNames.hyphenate(accessor.getName()));

        Set<String> matching = Sets.newHashSet();
        for(String candidate : candidates) {
            if(classNames.contains(candidate)) {
                matching.add(candidate);
            }
        }

        if(matching.size() == 1) {
            return matching.iterator().next();

        } else {
            if(matching.size() > 1) {
                logger.log(TreeLogger.Type.ERROR, "Ambiguous match for " + methodLabel + ": found " +
                        classNamesToString(matching));
            } else {
                logger.log(TreeLogger.Type.ERROR, "Unable to match " + methodLabel + " to a CSS class; considered " +
                        classNamesToString(candidates));
            }
        }
        return null;
    }


    public void validate(TreeLogger parentLogger, GenerationParameters options) throws UnableToCompleteException {

        Set<String> unmatched = Sets.difference(classNames, Sets.newHashSet(accessorToClassName.values()));

        if(!unmatched.isEmpty()) {
            TreeLogger.Type level = options.isAccessorRequiredForAllClasses() ? TreeLogger.Type.ERROR : TreeLogger.Type.WARN;
            TreeLogger logger = parentLogger.branch(level, "Unmatched classes in stylesheet:");
            for(String name : unmatched) {
                logger.log(level, name);
            }
            if(options.isAccessorRequiredForAllClasses()) {
                throw new UnableToCompleteException();
            }
        }
    }

    private String classNamesToString(Set<String> names) {
        StringBuilder s = new StringBuilder();
        for(String name : names) {
            if(s.length() > 0) {
                s.append(", ");
            }
            s.append(".").append(name);
        }
        return "[ " + s.toString() + " ]";
    }

    public String classNameForAccessor(String accessor) {
        return accessorToClassName.get(accessor);
    }
}
