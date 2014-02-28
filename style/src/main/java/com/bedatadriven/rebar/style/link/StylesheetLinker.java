package com.bedatadriven.rebar.style.link;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import com.bedatadriven.rebar.style.rebind.GssCompiler;
import com.bedatadriven.rebar.style.rebind.GssTree;
import com.bedatadriven.rebar.style.rebind.StylesheetGenerator;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.io.CharStreams;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.GeneratedResource;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SyntheticArtifact;


@LinkerOrder(Order.PRE)
public class StylesheetLinker extends AbstractLinker {

	@Override
	public String getDescription() {
		return "Links LESS fragments together";
	}

	@Override
	public ArtifactSet link(TreeLogger parentLogger, LinkerContext context,
			ArtifactSet artifacts) throws UnableToCompleteException {
		
		ArtifactSet toReturn = new ArtifactSet(artifacts);
		
		TreeLogger logger = parentLogger.branch(Type.INFO, "Linking CSS");
		
		for(CompilationResult compilationResult : artifacts.find(CompilationResult.class)) {
			SyntheticArtifact css = linkCss(logger, artifacts, compilationResult);
			if(css != null) {
				toReturn.add(css);
			}
		}
		
		return toReturn;
	}

	private SyntheticArtifact linkCss(TreeLogger parentLogger, ArtifactSet artifacts, CompilationResult compilationResult) throws UnableToCompleteException {
		
		TreeLogger logger = parentLogger.branch(Type.INFO, "Linking CSS for compilation result " + compilationResult.getPermutationId());
		
		// find all the CSS fragments that correspond to this compilations user agent
		String userAgent = userAgent(logger, compilationResult);
		
		StringBuilder css = new StringBuilder();
		for(GeneratedResource resource : artifacts.find(GeneratedResource.class)) {
			if(resource.getGenerator().equals(StylesheetGenerator.class) && 
					resource.getPartialPath().endsWith("_" + userAgent + ".css")) {
				logger.log(Type.INFO, "Adding " + resource.getPartialPath());
				append(logger, css, resource);
			}
		}
		
		logger.log(Type.INFO, "Preoptimization css size = " + css.length() + " b");
		
		if(css.length() == 0) {
			return null;
		} else {
			// Optimize
			//css = optimize(logger, compilationResult, css);
			
			// Write final CSS to the output
			return emitString(logger, css.toString(), compilationResult.getStrongName() + ".cache.css");
		}
	}

	private StringBuilder optimize(TreeLogger logger,
			CompilationResult compilationResult, StringBuilder css) throws UnableToCompleteException {

		UsedClassIndex index = new UsedClassIndex();
		index.addJS(logger, compilationResult);
		
		GssCompiler parser = new GssCompiler();
		GssTree tree = parser.compile(logger, css.toString());
		tree.prune(index.getPotentialClassNames());
		
		throw new UnsupportedOperationException("to finish");
		
	}

	private String userAgent(TreeLogger logger, CompilationResult compilationResult) {
		Set<String> values = Sets.newHashSet();
		for(Map<SelectionProperty, String> selectionMap : compilationResult.getPropertyMap()) {
			for(SelectionProperty property : selectionMap.keySet()) {
				if(property.getName().equals("user.agent")) {
					values.add(selectionMap.get(property));
				}
			}
		}
		logger.log(Type.INFO, "user.agent = " + values.toString());
		if(values.size() != 1) {
			logger.log(Type.ERROR, "Permutation is not bound to a single user.agent: " + values.toString());
		}
		return values.iterator().next();
	}

	private void append(TreeLogger logger, StringBuilder css,
			GeneratedResource resource) throws UnableToCompleteException {
		InputStream in = resource.getContents(logger);
		InputStreamReader reader = new InputStreamReader(in, Charsets.UTF_8);
		try {
			CharStreams.copy(reader, css);
			reader.close();
		} catch(IOException e) {
			logger.log(Type.ERROR, "I/O Exception while reading CSS fragment", e);
			throw new UnableToCompleteException();
		}
	}
}
