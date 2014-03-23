package com.bedatadriven.rebar.style.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.bedatadriven.rebar.style.client.IconSet;
import com.bedatadriven.rebar.style.client.Source;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.resources.client.CssResource;

import static com.google.gwt.core.ext.TreeLogger.Type.ERROR;

/**
 * Utility class to help resolve references to source files and other resources
 *
 */
public class SourceResolver {

    private final GeneratorContext context;
    private final JClassType interfaceType;

    public SourceResolver(GeneratorContext context, JClassType interfaceType) {
        this.context = context;
        this.interfaceType = interfaceType;
    }

    public String resolveSourceText(TreeLogger logger, String relativePath) throws UnableToCompleteException {
        String path = absolutePath(relativePath);
        return new String(resolveByteArray(logger, path), Charsets.UTF_8);
    }

    public byte[] resolveByteArray(TreeLogger logger, String path) throws UnableToCompleteException {
        Resource resource = context.getResourcesOracle().getResourceMap().get(path);
        if(resource == null) {
            logger.log(Type.ERROR, "Could not find resource at '" + path + "'");
            throw new UnableToCompleteException();
        }

        try(InputStream in = resource.openContents()) {
            return ByteStreams.toByteArray(in);
        } catch (IOException e) {
            logger.log(Type.ERROR, "Exception reading source at '" + path + "'", e);
            throw new UnableToCompleteException();
        }
    }

    public URL tryResolveURL(String sourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(absolutePath(sourceName));
    }

	public String absolutePath(String path) {
		return interfaceType.getPackage().getName().replace('.', '/') + '/' + path;
	}

}
