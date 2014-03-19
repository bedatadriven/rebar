package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.compiler.ast.Node;
import com.bedatadriven.rebar.cell.compiler.ast.XmlTextToken;
import com.bedatadriven.rebar.cell.rebind.eval.EvalException;
import com.bedatadriven.rebar.cell.rebind.expr.ExprNode;
import com.bedatadriven.rebar.cell.rebind.module.SourceProvider;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import fr.umlv.tatoo.runtime.ast.Token;
import fr.umlv.tatoo.runtime.lexer.LexingException;
import fr.umlv.tatoo.runtime.parser.ParsingException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Prints helpful diagnostic messages for exceptions
 */
public class DiagnosticPrinter {
    private final PrintStream writer;
    private final SourceProvider sourceProvider;

    public static final int LINE_WIDTH = 50;

    public DiagnosticPrinter(PrintStream writer, SourceProvider sourceProvider) {
        this.writer = writer;
        this.sourceProvider = sourceProvider;
    }

    public void printDetailedMessage(Throwable caught) {

        if(caught instanceof EvalException) {
            printEvalStackTrace((EvalException) caught);
            printStackTrace(caught);
            return;
        }

        Throwable root = findRootException(caught);

        if(root instanceof CompilerException) {

            CompilerException rootCe = (CompilerException) root;

            writer.println("Compilation failed: " + rootCe.getMessage());
            for(String trackBack : trackBackSourceUnits(caught)) {
                writer.println("  in " + trackBack);
            }
            writer.println();

            if(sourceProvider != null) {
                if(rootCe.getLocation() instanceof SourceLinePosition) {
                    printPrecedingLines((SourceLinePosition) rootCe.getLocation());
                }
            }

            printHelpMessage(rootCe);
            printStackTrace(rootCe);

            if(rootCe.getLocation() instanceof AstLocation) {
                AstLocation location = (AstLocation) rootCe.getLocation();
                printHeader("AST Tree");
                NodePrinter.dumpAstTree(location.getNode());
            }

        } else {

            writer.println("Compilation failed with an unexpected exception: " + root.getClass().getSimpleName());
            if(root.getMessage() != null) {
                writer.println("   " + root.getMessage());
            }
            printStackTrace(root);
        }
    }

    private void printEvalStackTrace(EvalException caught) {
        writer.println("Evaluation failed: " + caught.getMessage());
        for(ExprNode expr : caught.getExprStackTrace()) {
            writer.println("   at  " + expr );
        }
    }

    private void printStackTrace(Throwable root) {
        writer.println("\n\n");
        printHeader("Stack trace");
        root.printStackTrace(writer);
        printFooter();
    }


    private void printHelpMessage(CompilerException rootCompilationException) {

        String[] helpText = rootCompilationException.getHelpText();
        if(helpText != null) {
            writer.println();
            writer.println();
            printHeader("More information:");
            writer.println(Joiner.on("\n").join(helpText));
            printFooter();
        }
    }

    private void printFooter() {
        writer.println(Strings.repeat("=", LINE_WIDTH));
    }

    private void printHeader(String header) {
        printFooter();
        writer.println(header);
        writer.println(Strings.repeat("-", LINE_WIDTH));
    }


    public void printPrecedingLines(SourceLinePosition location) {

        List<String> lines;
        try {
            Reader reader = sourceProvider.open(location.getQualifiedModuleName());
            lines = CharStreams.readLines(reader);
        } catch(IOException e) {
            writer.println("Exception reading source: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        int lineInError = location.getLineNumber();
        int lineNumbersWidth = (int) Math.log10(lineInError) + 1;

        int startLine = Math.max(0, lineInError - 3);
        for(int i=startLine;i<=lineInError;++i) {
            writer.println(
                    Strings.padStart(Integer.toString(i+1), lineNumbersWidth, ' ') +
                            ":" + lines.get(i));
        }
        if(location.getColumnNumber() > 0) {
            writer.println(
                    Strings.repeat("-", location.getColumnNumber() + lineNumbersWidth) + "^");
        }
    }


    /**
     *
     * @return the root cause of the CompilerException, or null if the root cause was
     * unexpected.
     */
    private Throwable findRootException(Throwable caught) {

        if(caught instanceof SyntaxException) {
            return caught;
        }

        Throwable e = caught;
        while(e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    private List<String> trackBackSourceUnits(Throwable caught) {
        LinkedList<String> trackBack = Lists.newLinkedList();

        Throwable e = caught;
        while(e instanceof CompilerException) {
            SourceLocation location = ((CompilerException) e).getLocation();
            trackBack.addFirst(location.describeSourceLocation());
            e = e.getCause();
        }
        return trackBack;
    }

}
