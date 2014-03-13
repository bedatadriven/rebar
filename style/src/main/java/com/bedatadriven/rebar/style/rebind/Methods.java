package com.bedatadriven.rebar.style.rebind;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.resources.client.CssResource;

import java.util.List;
import java.util.Set;

/**
 * Utility functions for selecting methods from the marker interface
 */
class Methods {
    public static List<JMethod> getClassNameMethods(JClassType interfaceType) {

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
}
