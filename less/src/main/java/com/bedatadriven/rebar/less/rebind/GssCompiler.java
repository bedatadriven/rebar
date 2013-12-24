package com.bedatadriven.rebar.less.rebind;

import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

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
