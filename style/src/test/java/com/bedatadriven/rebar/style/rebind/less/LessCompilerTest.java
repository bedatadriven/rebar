package com.bedatadriven.rebar.style.rebind.less;

import com.bedatadriven.rebar.style.rebind.ConsoleTreeLogger;
import org.junit.Test;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import com.google.gwt.core.ext.UnableToCompleteException;


public class LessCompilerTest {

	private ConsoleTreeLogger logger = new ConsoleTreeLogger();

	@Test
	public void compiledParserLoads() throws Exception {

        LessCompiler.newCompiler();
    }

    @Test
    public void test() throws UnableToCompleteException {
        compile("test.less");
	}

    @Test
    public void withImports() throws UnableToCompleteException {
        String output = compile("nested-imports.less");

        assertThat(output, not(equalTo("undefined")));
    }

    @Test(expected = Exception.class)
	public void compileError() throws UnableToCompleteException {
		String output = compile("invalid.less");
	}

    private String compile(String resourceName) throws UnableToCompleteException {
        String output = new LessCompiler(logger).compile(getResource(resourceName));
        System.out.println(output);
        return output;
    }

}
