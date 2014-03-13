package com.bedatadriven.rebar.style.rebind;

import java.io.PrintWriter;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Writes the Java source code of the Stylesheet interface.
 */
public class StylesheetImplWriter  {

	private SourceWriter sw;
	private JClassType interfaceType;
    private String css;
    private String name;

    private Function<String, String> classNameMangler = Functions.identity();


    public StylesheetImplWriter(GeneratorContext context, JClassType interfaceType, String generatedSimpleSourceName,
                                PrintWriter pw, String css) {
		this.interfaceType = interfaceType;
		
		ClassSourceFileComposerFactory f = new ClassSourceFileComposerFactory(
				interfaceType.getPackage().getName(), generatedSimpleSourceName);

		f.addImplementedInterface(interfaceType.getQualifiedSourceName());

		this.sw = f.createSourceWriter(context, pw);
        this.name = interfaceType.getSimpleSourceName();
		this.css = css;
	}

    public void setClassNameMangler(Function<String, String> classNameMangler) {
        this.classNameMangler = classNameMangler;
    }

    public void write(TreeLogger logger) throws UnableToCompleteException {

		writeEnsureInjected();
		writeGetText();
        writeGetName();
		writeClassNameMethods();
		
		sw.commit(logger);	
	}
	

	private void writeEnsureInjected() {

        sw.println("private static boolean injected;");
        sw.println("public boolean ensureInjected() {");
        sw.indent();
        sw.println("if (!injected) {");
        sw.indentln("injected = true;");
        sw.indentln(StyleInjector.class.getName() + ".inject(\"" + Generator.escape(css) + "\");");
        sw.indentln("return true;");
        sw.println("}");
        sw.println("return false;");
        sw.outdent();
        sw.println("}");
	}

	private void writeGetText() throws UnableToCompleteException {
		sw.println("public String getText() {");
		sw.indentln("throw new UnsupportedOperationException();");
		sw.println("}");
	}

    private void writeGetName() throws UnableToCompleteException {
        sw.println("public String getName() {");
        sw.indentln("return " + quote(name) + ";");
        sw.println("}");
    }

    private void writeSimpleGetter(JMethod methodToImplement, String toReturn) {
		sw.print(methodToImplement.getReadableDeclaration(false, true, true, true, true));
		sw.println(" {");
		sw.indentln("return " + toReturn + ";");
		sw.println("}");
	}

    private void writeClassNameMethods() {
		for(JMethod method : Methods.getClassNameMethods(interfaceType)) {
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
			return classNameMangler.apply(method.getName());
		}
	}	
}
