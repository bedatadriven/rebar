package com.bedatadriven.rebar.less.rebind;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class LessResourceGenerator extends IncrementalGenerator {

	private String className(JMethod method) {
		ClassName className = method.getAnnotation(ClassName.class);
		if(className != null) {
			return className.value();
		} else {
			return method.getName();
		}
	}

	@Override
	public RebindResult generateIncrementally(TreeLogger logger,
			GeneratorContext context, String typeName)
					throws UnableToCompleteException {

		JClassType type = context.getTypeOracle().findType(typeName);
		String css = compileCSS(logger, context, type);

		String generatedSimpleSourceName = type.getSimpleSourceName() + "Impl";

		PrintWriter pw = context.tryCreate(logger, type.getPackage().getName(), type.getSimpleSourceName() + "Impl");

		ClassSourceFileComposerFactory f = new ClassSourceFileComposerFactory(
				type.getPackage().getName(), generatedSimpleSourceName);

		f.addImplementedInterface(type.getQualifiedSourceName());
		SourceWriter sw = f.createSourceWriter(context, pw);

		writeEnsureInjected(sw);
		writeGetText(logger, sw, css);
		writeClassNameMethods(sw, type);
		
		sw.commit(logger);
		
		return new RebindResult(RebindMode.USE_ALL_NEW, f.getCreatedClassName());
	}
 
	private void writeClassNameMethods(SourceWriter sw, JClassType type) {
		for(JMethod method : getClassNameMethods(type)) {
			String className = className(method);
			writeSimpleGetter(method, quote(className), sw);
		}
	}

	private String quote(String className) {
		return "\"" + escape(className) + "\"";
	}

	private String compileCSS(TreeLogger logger, GeneratorContext context, JClassType type) throws UnableToCompleteException {

		Preprocessor preprocessor = new Preprocessor();
		preprocessor.preprocess(logger, context, type);

		String css = compileLess(logger, preprocessor.asString());

		return css;
	}

	private String compileLess(TreeLogger parentLogger, String input) throws UnableToCompleteException {
		TreeLogger logger = parentLogger.branch(Type.INFO, "Compiling LESS...");
		try {
			Function<String, String> lessCompiler = LessCompilerFactory.create();
			String css = lessCompiler.apply(input);
			return css;
		} catch(Exception e) {
			logger.log(Type.ERROR, "Error compiling LESS: " + e.getMessage(), e);
			throw new UnableToCompleteException();
		}
	}


	@Override
	public long getVersionId() {
		return 1;
	}

	private List<JMethod> getClassNameMethods(JClassType type) {
		List<JMethod> methods = Lists.newArrayList();
		for(JMethod method : type.getOverridableMethods()) {
			if(isReturnTypeString(method.getReturnType().isClass()) && method.getParameters().length == 0 &&
					!method.getEnclosingType().getName().equals("CssResource")) {
				methods.add(method);
			}
		}
		return methods;
	}

	protected boolean isReturnTypeString(JClassType classReturnType) {
		return (classReturnType != null
				&& String.class.getName().equals(classReturnType.getQualifiedSourceName()));
	}

	protected void writeEnsureInjected(SourceWriter sw) {
		sw.println("private boolean injected;");
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

	protected void writeGetName(JMethod method, SourceWriter sw) {
		sw.println("public String getName() {");
		sw.indentln("return \"" + method.getName() + "\";");
		sw.println("}");
	}

	protected void writeGetText(TreeLogger logger, SourceWriter sw, String css) throws UnableToCompleteException {
		sw.println("public String getText() {");
		sw.indentln("return " + quote(css) + ";");
		sw.println("}");
	}

	protected void writeSimpleGetter(JMethod methodToImplement, String toReturn, SourceWriter sw) {
		sw.print(methodToImplement.getReadableDeclaration(false, true, true, true, true));
		sw.println(" {");
		sw.indentln("return " + toReturn + ";");
		sw.println("}");
	}
}
