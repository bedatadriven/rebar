package com.bedatadriven.rebar.cell.rebind.module;

import java.io.IOException;
import java.io.Reader;

/**
 * Provides Cell Sources
 */
public interface SourceProvider {

    Reader open(String qualifiedName) throws IOException;


}
