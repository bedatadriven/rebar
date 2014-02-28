package com.bedatadriven.rebar.style.rebind;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import com.google.common.css.compiler.ast.CssFunctionNode;
import com.google.common.css.compiler.ast.CssStringNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import com.google.common.io.ByteStreams;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.io.Resources;

public class EmitResources extends DefaultTreeVisitor {


	private MutatingVisitController visitController;
	private TreeLogger logger;
	private boolean errors = false;
	private GeneratorContext context;

	public EmitResources(MutatingVisitController visitController, GeneratorContext context, TreeLogger parentLogger) {
		this.visitController = visitController;
		this.logger = parentLogger.branch(Type.DEBUG, "Preparing fonts...");
		this.context = context;
	}


	@Override
	public boolean enterFunctionNode(CssFunctionNode value) {
		if(value.getFunctionName().equals("url")) {

			logger.log(Type.DEBUG, "Encountered URL: " + value.toString());
			if( value.getArguments().getChildren().size() == 1 && value.getArguments().getChildAt(0) instanceof CssStringNode) {
				CssStringNode urlString = (CssStringNode) value.getArguments().getChildAt(0);

				try {
					if(urlString.getValue().endsWith(".ttf")) {
						emitFont(urlString);
					}

				} catch(Exception e) {
					logger.log(Type.ERROR, "Error while processing font resource", e);
					errors = true;
				}
			}
		}
		return false;
	}

	private void emitFont(CssStringNode urlString)
			throws UnableToCompleteException, IOException {
		
		URL url = ResourceResolver.getResourceUrl(logger, urlString.getValue());

		byte[] data = Resources.toByteArray(url);

		String name = Util.computeStrongName(data) + ".cache.ttf";

		logger.log(Type.INFO, "Writing font to " + name);

		OutputStream out = context.tryCreateResource(logger, name);

		if(out != null) {
			// already written out
			ByteStreams.copy(new ByteArrayInputStream(data), out);			
			context.commitResource(logger, out);
		}

		urlString.setValue(name);
	}

	public void runPass() {
		visitController.startVisit(this);
	}

	public boolean hasErrors() {
		return errors;
	}
}
