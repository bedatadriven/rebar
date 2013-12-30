package com.bedatadriven.rebar.less.link;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.js.JsParser;
import com.google.gwt.dev.js.JsParserException;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsScope;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.dev.js.ast.JsVisitor;

/**
 * Builds an index of CSS classes actually used.
 */
public class UsedClassIndex {

	private Set<String> potentialClassNames = Sets.newHashSet();
	

	public void addJS(TreeLogger logger, CompilationResult compilationResult) throws UnableToCompleteException {
		String[] scripts = compilationResult.getJavaScript();
		for(int i=0;i!=scripts.length;++i) {
			logger.log(Type.DEBUG, "Parsing JS block " + i + " for class names...");
			addJS(logger, scripts[0]);
		}
	}
	
	public void addJS(TreeLogger logger, String js) throws UnableToCompleteException {
		Reader r = new StringReader(js);
		JsProgram jsProgram = new JsProgram();
		JsScope topScope = jsProgram.getScope();

		try {
			SourceInfo sourceInfo = jsProgram.createSourceInfo(1, "fragment.js");
			JsParser.parseInto(sourceInfo, topScope, jsProgram.getGlobalBlock(), r);
			
			new LiteralCollectingVisitor().accept(jsProgram);
			
		} catch (IOException e) {
			throw new RuntimeException("Unexpected error reading in-memory stream", e);
		} catch (JsParserException e) {
			logger.log(TreeLogger.ERROR, "Unable to parse JavaScript", e);
			throw new UnableToCompleteException();
		}

	}
	
	private class LiteralCollectingVisitor extends JsVisitor {

		@Override
		public void endVisit(JsStringLiteral x, JsContext ctx) {
			potentialClassNames.addAll(Arrays.asList(x.getValue().split("\\w+")));
		}
	}
	
	public Set<String> getPotentialClassNames() {
		return potentialClassNames;
	}
}
