package com.bedatadriven.rebar.style.rebind;

import java.io.PrintWriter;
import java.util.Map;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Writes the Java source code of the Stylesheet interface.
 */
public class StylesheetImplWriter {

    private GenerationParameters options;
    private JClassType interfaceType;

    private String generatedSimpleSourceName;
    private String qualifiedSourceName;

	private SourceWriter sw;

    public StylesheetImplWriter(GenerationParameters options, JClassType interfaceType) {
        this.options = options;
        this.interfaceType = interfaceType;
        generatedSimpleSourceName = generatedSourceName();
        qualifiedSourceName = interfaceType.getPackage().getName() + "." + generatedSimpleSourceName;
    }

    /**
     * Compose a unique name for the implementation class for each user agent.
     */
    private String generatedSourceName() {
        return interfaceType.getSimpleSourceName() + "Impl_" +
                options.getUserAgent().name().toLowerCase();
    }

    public String getGeneratedSimpleSourceName() {
        return generatedSimpleSourceName;
    }

    public String getQualifiedSourceName() {
        return qualifiedSourceName;
    }

    public boolean tryCreate(GeneratorContext context, TreeLogger logger) {

        PrintWriter pw = context.tryCreate(logger, interfaceType.getPackage().getName(), generatedSimpleSourceName);
        if(pw == null) {
            // Already exists!
            return false;
        } else {
            ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(
                    interfaceType.getPackage().getName(), generatedSimpleSourceName);

            factory.addImplementedInterface(interfaceType.getQualifiedSourceName());
            this.sw = factory.createSourceWriter(context, pw);

            return true;
        }
    }

	public void writeEnsureInjected() {
        sw.println("private static boolean injected;");
        sw.println("public boolean ensureInjected() {");
        sw.indent();
        sw.println("if (!injected) {");
        sw.indentln("injected = true;");
        sw.indentln(StyleInjector.class.getName() + ".inject(getText());");
        sw.indentln("return true;");
        sw.println("}");
        sw.println("return false;");
        sw.outdent();
        sw.println("}");
	}

	public void writeGetText(String css) throws UnableToCompleteException {
		sw.println("public String getText() {");
        sw.indentln("return \"" + Generator.escape(css) + "\";");
		sw.println("}");
	}

    public void writeGetName() throws UnableToCompleteException {
        sw.println("public String getName() {");
        sw.indentln("return " + quote(interfaceType.getSimpleSourceName()) + ";");
        sw.println("}");
    }

    public void writeClassNameMethods(Map<String, String> accessorToClassMap) {
        for(JMethod method : interfaceType.getOverridableMethods()) {
            if(accessorToClassMap.containsKey(method.getName())) {
                String className = accessorToClassMap.get(method.getName());
                writeSimpleGetter(method, quote(className));
            }
        }
	}

    private void writeSimpleGetter(JMethod methodToImplement, String toReturn) {
        sw.print(methodToImplement.getReadableDeclaration(false, true, true, true, true));
        sw.println(" {");
        sw.indentln("return " + toReturn + ";");
        sw.println("}");
    }

    public void commit(TreeLogger logger) {
        sw.commit(logger);
    }

	private String quote(String text) {
		return "\"" + Generator.escape(text) + "\"";
	}
}
