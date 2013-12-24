package com.bedatadriven.rebar.less;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.lesscss.LessSource;

import com.bedatadriven.rebar.less.rebind.GssCompiler;
import com.bedatadriven.rebar.less.rebind.GssTree;
import com.bedatadriven.rebar.less.rebind.LessCompiler;
import com.bedatadriven.rebar.less.rebind.LessException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gwt.core.ext.UnableToCompleteException;


public class BootstrapTest {

	@Test
	public void testCompile() throws IOException, LessException, UnableToCompleteException {
		
		ConsoleTreeLogger logger = new ConsoleTreeLogger();
		
		LessSource source = new LessSource(new File("src/test/less/bootstrap-3.0.3/bootstrap.less"));
		
		LessCompiler lessCompiler = new LessCompiler();
		lessCompiler.init(logger);
		
		String lessOutput = lessCompiler.compile(logger, source.getNormalizedContent());
		Files.write(lessOutput, new File("target/bootstrap.css"), Charsets.UTF_8);
		
		GssCompiler gssCompiler = new GssCompiler();
		GssTree tree = gssCompiler.compile(logger, lessOutput);
		tree.finalizeTree(logger);
		tree.optimize(logger, "safari");
		
		
	}
	
}
