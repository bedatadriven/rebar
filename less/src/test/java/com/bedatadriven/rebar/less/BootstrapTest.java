package com.bedatadriven.rebar.less;

import java.io.File;

import org.junit.Test;

import com.bedatadriven.rebar.less.rebind.GssCompiler;
import com.bedatadriven.rebar.less.rebind.GssTree;
import com.bedatadriven.rebar.less.rebind.LessCompilerFactory;
import com.bedatadriven.rebar.less.rebind.LessInput;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;


public class BootstrapTest {

	@Test
	public void testClassCompile() throws Exception {

		ConsoleTreeLogger logger = new ConsoleTreeLogger();
		
		Function<LessInput, String> compiler = LessCompilerFactory.create();
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		System.out.println("Less parser loaded in "  + stopwatch);
		
		stopwatch.reset().start();
		
		LessInput input = new LessInput();
		input.setFile("src/test/less/bootstrap-3.0.3/bootstrap.less");
		input.setUserAgent("safari");
		String lessOutput = compiler.apply(input);
	
		System.out.println("Less compiled in "  + stopwatch);
		
		Files.write(lessOutput, new File("target/bootstrap.css"), Charsets.UTF_8);
		
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, lessOutput);
		tree.finalizeTree(logger);
		tree.optimize(logger, "safari");

	}
	
}
