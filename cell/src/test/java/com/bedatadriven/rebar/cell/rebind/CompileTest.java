package com.bedatadriven.rebar.cell.rebind;

import com.bedatadriven.rebar.cell.rebind.eval.StaticRenderer;
import com.bedatadriven.rebar.cell.rebind.module.*;
import com.bedatadriven.rebar.cell.rebind.module.Compiler;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import static org.junit.Assert.assertThat;


public class CompileTest {

    private Compiler compiler;
    private ResourceSourceProvider sourceProvider;

    @Test
    public void test() throws IOException {

        sourceProvider = new ResourceSourceProvider();

        compiler = new Compiler(sourceProvider);

        try {

            Module module = compiler.getModule("simple.Test");
            Cell cell = module.getCell("PageContainer");

            StaticRenderer renderer = new StaticRenderer();
            renderer.renderCell(cell);

            System.out.println("\nRENDERED:");
            System.out.println(renderer.toHtml());

        } catch(Exception ce) {
            compiler.getDiagnosticPrinter().printDetailedMessage(ce);
        }
    }

    private static class ResourceSourceProvider implements SourceProvider {

        @Override
        public Reader open(String qualifiedName) throws IOException {
            URL url = Resources.getResource(qualifiedName.replace('.', '/') + ".cell");
            return new InputStreamReader(url.openStream());
        }
    }
}
