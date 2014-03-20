package com.bedatadriven.rebar.style.rebind.passes;

import com.bedatadriven.rebar.style.rebind.ConsoleTreeLogger;
import com.bedatadriven.rebar.style.rebind.SourceResolver;
import com.bedatadriven.rebar.style.rebind.gss.EmitResources;
import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.common.css.compiler.passes.PrettyPrinter;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.easymock.EasyMock.*;

public class EmitFontResourcesTest {

	private TreeLogger logger = new ConsoleTreeLogger();

	@Test
    @Ignore
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


		new EmitResources(tree.getMutatingVisitController(), context, logger, null).runPass();
		
		verify(context);
		
		PrettyPrinter printer = new PrettyPrinter(tree.getVisitController());
		printer.runPass();
		System.out.println(printer.getPrettyPrintedString());
	}
}
