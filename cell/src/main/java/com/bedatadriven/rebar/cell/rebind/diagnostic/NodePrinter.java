package com.bedatadriven.rebar.cell.rebind.diagnostic;

import com.bedatadriven.rebar.cell.compiler.ast.Node;
import com.google.common.base.Strings;
import fr.umlv.tatoo.runtime.ast.Token;

import java.io.PrintStream;
import java.io.PrintWriter;


public class NodePrinter {

    public static void dumpAstTree(Node node) {
        dumpAstTree(System.out, 0, node);
    }

    public static void dumpAstTree(PrintStream pw, int indent, Node node) {
        pw.print(Strings.repeat("   ", indent));
        pw.print(node.getClass().getSimpleName());
        if(node instanceof Token) {
            pw.print(" ");
            pw.print("'");
            pw.print(formatTokenValue((Token) node));
            pw.print("'");

        }
        pw.println();

        for(Node child : node.nodeList()) {
            dumpAstTree(pw, indent+1, child);
        }
    }

    public static Object formatTokenValue(Token node) {

        Object text = node.getValue();
        if(text instanceof String) {
            return text.toString().replace("\n", "\\n");
        } else if(text == null) {
            return "";
        } else {
            return text.toString();
        }
    }

}
