package com.bedatadriven.rebar.style;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.*;

import com.bedatadriven.rebar.style.rebind.GssCompiler;
import com.bedatadriven.rebar.style.rebind.GssTree;
import com.bedatadriven.rebar.style.rebind.LessCompilerFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import com.google.gwt.core.ext.UnableToCompleteException;


public class CompileTest {

	private ConsoleTreeLogger logger = new ConsoleTreeLogger();

	@Test
	public void testClassCompile() throws Exception {

		String source = "@color: #4D926F;\n" +
				"#header {  color: @color;  }\n" +
				"h2 {  color: @color; }\n";
		
		Function<String, String> compiler = LessCompilerFactory.create();
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		System.out.println("Less parser loaded in "  + stopwatch);
		
		stopwatch.reset().start();
		
		String lessOutput = compiler.apply(source);
	
		System.out.println("Less compiled in "  + stopwatch);
		
		Files.write(lessOutput, new File("target/test.css"), Charsets.UTF_8);
		
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, lessOutput);
		tree.finalizeTree(logger);
		//tree.optimize(logger, "safari");
	}
	
	@Test(expected = Exception.class)
	public void compileError() {
		String source = "#header {  color: @color;  }\n";
		
		Function<String, String> compiler = LessCompilerFactory.create();
		compiler.apply(source);
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
}
