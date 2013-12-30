package com.bedatadriven.rebar.style.rebind;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Writes the Java source code of the Stylesheet interface.
 */
public class StylesheetImplWriter {

	private SourceWriter sw;
	private JClassType interfaceType;
	
	
	public StylesheetImplWriter(GeneratorContext context, JClassType interfaceType, String generatedSimpleSourceName,
			PrintWriter pw) {
		this.interfaceType = interfaceType;
		
		ClassSourceFileComposerFactory f = new ClassSourceFileComposerFactory(
				interfaceType.getPackage().getName(), generatedSimpleSourceName);

		f.addImplementedInterface(interfaceType.getQualifiedSourceName());
		this.sw = f.createSourceWriter(context, pw);
	}
	
	public void write(TreeLogger logger) throws UnableToCompleteException {

		writeEnsureInjected();
		writeGetText();
		writeClassNameMethods();
		
		sw.commit(logger);	
	}
	

	private void writeEnsureInjected() {
		sw.println("public boolean ensureInjected() {");
		sw.indent();
		sw.println("return com.bedatadriven.rebar.style.client.StylesheetInjector.ensureInjected();");
		sw.outdent();
		sw.println("}");
	}

	private void writeGetText() throws UnableToCompleteException {
		sw.println("public String getText() {");
		sw.indentln("throw new UnsupportedOperationException();");
		sw.println("}");
	}

	private void writeSimpleGetter(JMethod methodToImplement, String toReturn) {
		sw.print(methodToImplement.getReadableDeclaration(false, true, true, true, true));
		sw.println(" {");
		sw.indentln("return " + toReturn + ";");
		sw.println("}");
	}
	
	private List<JMethod> getClassNameMethods() {
		List<JMethod> methods = Lists.newArrayList();
		for(JMethod method : interfaceType.getOverridableMethods()) {
			if(isReturnTypeString(method.getReturnType().isClass()) && method.getParameters().length == 0 &&
					!method.getEnclosingType().getName().equals(CssResource.class.getSimpleName())) {
				methods.add(method);
			}
		}
		return methods;
	}

	private boolean isReturnTypeString(JClassType classReturnType) {
		return (classReturnType != null
				&& String.class.getName().equals(classReturnType.getQualifiedSourceName()));
	}

	private void writeClassNameMethods() {
		for(JMethod method : getClassNameMethods()) {
			String className = className(method);
			writeSimpleGetter(method, quote(className));
		}
	}
	
	private String quote(String text) {
		return "\"" + Generator.escape(text) + "\"";
	}

	private String className(JMethod method) {
		ClassName className = method.getAnnotation(ClassName.class);
		if(className != null) {
			return className.value();
		} else {
			return method.getName();
		}
	}	
}
