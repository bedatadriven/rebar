package com.bedatadriven.rebar.style;

import java.io.File;
import java.net.URL;

import com.bedatadriven.rebar.style.rebind.css.LessCompilerContext;
import com.google.common.io.Resources;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import com.bedatadriven.rebar.style.rebind.css.GssCompiler;
import com.bedatadriven.rebar.style.rebind.css.GssTree;
import com.bedatadriven.rebar.style.rebind.css.LessCompilerFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import com.google.gwt.core.ext.UnableToCompleteException;


public class CompileTest {

	private ConsoleTreeLogger logger = new ConsoleTreeLogger();

	@Test
	public void testClassCompile() throws Exception {


        LessCompilerContext lessContext = contextForSource("test.less");
		
		Function<LessCompilerContext, String> compiler = LessCompilerFactory.create();
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		System.out.println("Less parser loaded in "  + stopwatch);
		
		stopwatch.reset().start();
		
		String lessOutput = compiler.apply(lessContext);
	    System.out.println(lessOutput);
		System.out.println("Less compiled in "  + stopwatch);
		
		Files.write(lessOutput, new File("target/test.css"), Charsets.UTF_8);
		
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, lessOutput);
		tree.finalizeTree(logger);
		//tree.optimize(logger, "safari");
	}

    @Test
    public void withImports() {
        Function<LessCompilerContext, String> compiler = LessCompilerFactory.create();
        String css = compiler.apply(contextForSource("nested-imports.less"));
        System.out.println(css);
        assertThat(css, not(equalTo("undefined")));
    }

    @Test(expected = Exception.class)
	public void compileError() {
		Function<LessCompilerContext, String> compiler = LessCompilerFactory.create();
		compiler.apply(contextForSource("invalid.less"));
	}
	
	@Test
	public void collectClassNames() throws UnableToCompleteException {
		
		String css = ".foobar { font-weight: bold; }\n" +
					 ".bazinga { color: blue; }\n";
		
		
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, css);
		tree.finalizeTree(logger);
	//	tree.optimize(logger, "safari");
		tree.renameClasses();
		
		assertTrue(tree.getMappings().containsKey("foobar"));
		assertTrue(tree.getMappings().containsKey("bazinga"));
		
	}

    private LessCompilerContext contextForSource(String resourceName) {
        URL resource = Resources.getResource(resourceName);

        return new LessCompilerContext(new ConsoleTreeLogger(), resource.getFile());
    }
}
