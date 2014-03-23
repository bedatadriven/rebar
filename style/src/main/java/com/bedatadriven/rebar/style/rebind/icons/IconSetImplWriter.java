package com.bedatadriven.rebar.style.rebind.icons;

import com.bedatadriven.rebar.style.client.impl.IconUtil;
import com.bedatadriven.rebar.style.rebind.GenerationParameters;
import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.List;

/**
 * Writes the java source to implement the IconSet interface
 */
public class IconSetImplWriter {

    private GenerationParameters options;
    private JClassType interfaceType;

    private String generatedSimpleSourceName;
    private String qualifiedSourceName;

    private SourceWriter sw;

    public IconSetImplWriter(GenerationParameters options, JClassType interfaceType, IconStrategy strategy) {
        this.options = options;
        this.interfaceType = interfaceType;
        generatedSimpleSourceName = interfaceType.getSimpleSourceName()  + "Impl_" + strategy.getName();
        qualifiedSourceName = interfaceType.getPackage().getName() + "." + generatedSimpleSourceName;
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

    public void writeEnsureInjected(IconArtifacts artifacts) {
        sw.println("private static boolean injected;");
        sw.println("public boolean ensureInjected() {");
        sw.indent();
        sw.println("if (!injected) {");
        sw.indentln("injected = true;");
        sw.println(StyleInjector.class.getName() + ".inject(" + styleSheetExpr(artifacts) + ");");

        for(String innerSvg : artifacts.getInlineSvgDocuments()) {
            sw.println(IconUtil.class.getName() + ".injectSvgDocument(" + quote(innerSvg) + ");");
        }

        sw.println("return true;");
        sw.outdent();
        sw.println("}");
        sw.println("return false;");
        sw.outdent();
        sw.println("}");
    }

    @VisibleForTesting
    static String styleSheetExpr(IconArtifacts artifacts) {

        String staticRootPlaceholder = "$$PLACEHOLDER$$";

        String escaped = "\"" + Generator.escape(
                artifacts.getStylesheet(staticRootPlaceholder)) + "\"";

        escaped = escaped.replace(staticRootPlaceholder,
                 "\" + com.google.gwt.core.client.GWT.getModuleBaseForStaticFiles() + \"");

        return escaped;
    }

    public void writeClassNameMethods(TreeLogger logger, IconContext context, List<Icon> icons) throws UnableToCompleteException {
        for(Icon icon: icons) {
            JMethod method = null;
            try {
                method = interfaceType.getMethod(icon.getAccessorName(), new JType[0]);
            } catch (NotFoundException e) {
                logger.log(TreeLogger.Type.ERROR, "Could not find method for accessor " + icon.getAccessorName(), e);
                throw new UnableToCompleteException();
            }
            writeSimpleGetter(method, quote(context.getBaseClassName() + " " + icon.getClassName()));
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
