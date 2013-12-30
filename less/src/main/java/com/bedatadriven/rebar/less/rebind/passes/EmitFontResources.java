package com.bedatadriven.rebar.less.rebind.passes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import com.bedatadriven.rebar.less.rebind.ResourceResolver;
import com.google.common.css.compiler.ast.CssDeclarationNode;
import com.google.common.css.compiler.ast.CssFontFaceNode;
import com.google.common.css.compiler.ast.CssFunctionNode;
import com.google.common.css.compiler.ast.CssNode;
import com.google.common.css.compiler.ast.CssPropertyValueNode;
import com.google.common.css.compiler.ast.CssStringNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import com.google.common.io.ByteStreams;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.linker.GeneratedResource;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.io.Resources;

public class EmitFontResources extends DefaultTreeVisitor {


	private MutatingVisitController visitController;
	private TreeLogger logger;
	private boolean errors = false;
	private GeneratorContext context;

	public EmitFontResources(MutatingVisitController visitController, GeneratorContext context, TreeLogger parentLogger) {
		this.visitController = visitController;
		this.logger = parentLogger.branch(Type.DEBUG, "Preparing fonts...");
		this.context = context;
	}

	@Override
	public boolean enterFontFace(CssFontFaceNode node) {
		
		TreeLogger fontFaceLogger = logger.branch(Type.DEBUG, "Inspecting " + node.toString());
		
		for(CssNode valueNode :  node.getBlock().getChildren()) {
			if(valueNode instanceof CssDeclarationNode) {
				CssDeclarationNode decl = (CssDeclarationNode)valueNode;
				if(decl.getPropertyName().getValue().equals("src")) {
					updateSource(fontFaceLogger, decl.getPropertyValue());
				}
			}
		}
		return true;
	}

	private void updateSource(TreeLogger logger, CssPropertyValueNode value) {
		if(value.getChildren().size() != 1 ||
				!(value.getChildAt(0) instanceof CssFunctionNode) ||
				!(((CssFunctionNode) value.getChildAt(0)).getFunctionName().equals("url"))) {
			logger.log(Type.ERROR, "Expected single url() value for src property in @FontFace");
			errors = true;
			return;
		}
		
		CssFunctionNode urlFn = (CssFunctionNode) value.getChildAt(0);
		CssStringNode urlString = (CssStringNode) urlFn.getArguments().getChildAt(0);
		
		URL url;
		try {
			url = ResourceResolver.getResourceUrl(logger, urlString.getValue());

		
			byte[] data = Resources.toByteArray(url);
		
			String name = Util.computeStrongName(data) + ".cache.ttf";
			
			logger.log(Type.INFO, "Writing font to " + name);
			
			OutputStream out = context.tryCreateResource(logger, name);
			
			if(out != null) {
				ByteStreams.copy(new ByteArrayInputStream(data), out);			
				context.commitResource(logger, out);
				urlString.setValue(name);
			}
			
		} catch(Exception e) {
			logger.log(Type.ERROR, "Error while processing font resource", e);
			errors = true;
		}
	}

	public void runPass() {
		visitController.startVisit(this);
	}

	public boolean hasErrors() {
		return errors;
	}
}
