package com.bedatadriven.rebar.less.rebind.passes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.bedatadriven.rebar.less.ConsoleTreeLogger;
import com.bedatadriven.rebar.less.rebind.LoggingErrorManager;
import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.common.css.compiler.passes.CreateStandardAtRuleNodes;
import com.google.common.css.compiler.passes.PrettyPrinter;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public class EmitFontResourcesTest {

	private TreeLogger logger = new ConsoleTreeLogger();

	@Test
	public void test() throws UnableToCompleteException, GssParserException {

		String css = 
				"@font-face {\n" + 
						"font-family: 'Glyphicons Halflings';\n" + 
						"src: url('com/bedatadriven/rebar/less/rebind/passes/glyphicons-halflings-regular.ttf');\n" +
						"}";
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GeneratorContext context = createMock(GeneratorContext.class);
		expect(context.tryCreateResource(isA(TreeLogger.class), isA(String.class))).andReturn(out);
		expect(context.commitResource(isA(TreeLogger.class), eq(out))).andReturn(null);
		replay(context);

		CssTree tree = new GssParser(new SourceCode("test.css", css)).parse();
		
		new CreateStandardAtRuleNodes(tree.getMutatingVisitController(), new LoggingErrorManager(logger)).runPass();
		new EmitFontResources(tree.getMutatingVisitController(), context, logger).runPass();
		
		verify(context);
		
		PrettyPrinter printer = new PrettyPrinter(tree.getVisitController());
		printer.runPass();
		System.out.println(printer.getPrettyPrintedString());
	}
}
