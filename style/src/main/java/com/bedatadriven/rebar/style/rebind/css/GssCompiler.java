package com.bedatadriven.rebar.style.rebind.css;

import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Wrapper around Google (Closure) Style-sheet Compiler.
 *
 * <p>We use GSS to post-process and optimize the CSS</p>
 */
public class GssCompiler {

	public GssTree compile(TreeLogger logger, String css) throws UnableToCompleteException {

		TreeLogger branchLogger = logger.branch(TreeLogger.DEBUG,
				"Parsing LESS output as GSS stylesheet");

		SourceCode lessOutput = new SourceCode("less.css", css);

		try {
			return new GssTree(new GssParser(lessOutput).parse());
		} catch (GssParserException e) {
			branchLogger.log(TreeLogger.ERROR, "Unable to parse CSS", e);
			throw new UnableToCompleteException();
		}
	}

}
