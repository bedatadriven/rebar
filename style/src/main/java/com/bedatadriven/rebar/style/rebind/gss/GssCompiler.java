package com.bedatadriven.rebar.style.rebind.gss;

import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.common.css.compiler.passes.*;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.util.Collections;
import java.util.Set;

/**
 * Wrapper around Google (Closure) Style-sheet Compiler.
 *
 * <p>We use GSS to post-process and optimize the CSS</p>
 */
public class GssCompiler {

    private Set<String> allowedAtRules = Collections.emptySet();

    public GssTree compile(TreeLogger logger, String css) throws UnableToCompleteException {

		TreeLogger branchLogger = logger.branch(TreeLogger.DEBUG,
				"Parsing LESS output as GSS stylesheet");

		SourceCode lessOutput = new SourceCode("less.css", css);

		try {
            CssTree tree = new GssParser(lessOutput).parse();
            finalizeTree(logger, tree);
            return new GssTree(tree);

		} catch (GssParserException e) {
			branchLogger.log(TreeLogger.ERROR, "Unable to parse CSS", e);
			throw new UnableToCompleteException();
		}
	}

    private void finalizeTree(TreeLogger logger, CssTree cssTree) {
        LoggingErrorManager errorManager = new LoggingErrorManager(
                logger.branch(TreeLogger.Type.INFO, "Finalizing Closure Stylesheet"));

        new CreateStandardAtRuleNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
        new CreateMixins(cssTree.getMutatingVisitController(), errorManager).runPass();
        new CreateDefinitionNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
        new CreateConstantReferences(cssTree.getMutatingVisitController()).runPass();
        new CreateConditionalNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
        new CreateComponentNodes(cssTree.getMutatingVisitController(), errorManager).runPass();

        new HandleUnknownAtRuleNodes(cssTree.getMutatingVisitController(), errorManager,
                allowedAtRules , true, false).runPass();
        new ProcessKeyframes(cssTree.getMutatingVisitController(), errorManager, true, true).runPass();
        new ProcessRefiners(cssTree.getMutatingVisitController(), errorManager, true).runPass();

        new ProcessComponents<Object>(cssTree.getMutatingVisitController(), errorManager).runPass();
    }
}
