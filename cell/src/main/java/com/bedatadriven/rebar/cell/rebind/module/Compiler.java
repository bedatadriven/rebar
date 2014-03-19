package com.bedatadriven.rebar.cell.rebind.module;

import com.bedatadriven.rebar.cell.compiler.ast.Script;
import com.bedatadriven.rebar.cell.compiler.lexer.RuleEnum;
import com.bedatadriven.rebar.cell.compiler.parser.NonTerminalEnum;
import com.bedatadriven.rebar.cell.compiler.parser.ProductionEnum;
import com.bedatadriven.rebar.cell.compiler.parser.TerminalEnum;
import com.bedatadriven.rebar.cell.compiler.parser.VersionEnum;
import com.bedatadriven.rebar.cell.compiler.tools.Analyzers;
import com.bedatadriven.rebar.cell.compiler.tools.GrammarEvaluator;
import com.bedatadriven.rebar.cell.compiler.tools.TerminalEvaluator;
import com.bedatadriven.rebar.cell.rebind.diagnostic.CompilerException;
import com.bedatadriven.rebar.cell.rebind.diagnostic.DiagnosticPrinter;
import com.bedatadriven.rebar.cell.rebind.diagnostic.LexerLocation;
import com.bedatadriven.rebar.cell.rebind.diagnostic.SyntaxException;
import com.google.common.collect.Maps;
import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LexingException;
import fr.umlv.tatoo.runtime.parser.ParsingException;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.runtime.tools.builder.Builder;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Compiles a series of cell modules
 */
public class Compiler {

    public static final String LANGUAGE_NAME = "cell";

    private SourceProvider sourceProvider;
    private Map<String, Module> modules = Maps.newHashMap();
    private DiagnosticPrinter diagnosticPrinter;


    public Compiler(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
        this.diagnosticPrinter = new DiagnosticPrinter(System.err, sourceProvider);
    }

    private Module tryReadModule(String qualifiedName) throws IOException {
        Reader reader = sourceProvider.open(qualifiedName);
        LocationTracker locationTracker = new LocationTracker();

        ReaderWrapper buffer = new ReaderWrapper(reader, locationTracker);

        AstHandler astHandler = new AstHandler(qualifiedName, qualifiedName, locationTracker);
        Lexer<ReaderWrapper> lexer = createAnalyzer(buffer, astHandler);

        try {
            lexer.run();
        } catch(ParsingException e)  {
            throw new SyntaxException(new LexerLocation(qualifiedName, locationTracker), e);
        } catch(LexingException e) {
            throw new SyntaxException(new LexerLocation(qualifiedName, locationTracker), e);
        }
        Script script = astHandler.getScript();

        return new Module(qualifiedName, script);
    }

    public DiagnosticPrinter getDiagnosticPrinter() {
        return diagnosticPrinter;
    }

    public static <B extends TokenBuffer<CharSequence> & LexerBuffer> Lexer<B> createAnalyzer(B buffer, AstHandler astHandler) {

        TerminalEvaluator<CharSequence> terminalEvaluator = astHandler;
        GrammarEvaluator grammarEvaluator = astHandler;
        Builder.AnalyzerParserBuilder<RuleEnum, B, TerminalEnum, NonTerminalEnum, ProductionEnum, VersionEnum> builder =
                Analyzers.analyzerTokenBufferBuilder(buffer,
                        terminalEvaluator,
                        grammarEvaluator,
                        new SemanticStack()).
                        expert().
                        advanced();
        return builder
                .createAnalyzer()
                .getLexer();
    }


    public Module getModule(String qualifiedModuleName) {
        Module module = modules.get(qualifiedModuleName);
        if(module == null) {
            try {
                module = tryReadModule(qualifiedModuleName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            modules.put(qualifiedModuleName, module);
        }
        return module;
    }

    public Cell getCell(String qualifiedName) {
        int lastDot = qualifiedName.lastIndexOf('.');
        if(lastDot == -1) {
            throw new IllegalArgumentException("Expected qualified source name, got: " + qualifiedName);
        }
        String qualifiedModuleName = qualifiedName.substring(0, lastDot);
        String cellName = qualifiedName.substring(lastDot+1);

        return getModule(qualifiedModuleName).getCell(cellName);
    }
}
