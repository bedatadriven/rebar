package com.bedatadriven.rebar.less.rebind;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.thirdparty.guava.common.io.Resources;

public class ResourceResolver {

	public static String resourceToString(TreeLogger logger, String path) throws UnableToCompleteException {
		URL url = getResourceUrl(logger, path);
		try {
			return Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			logger.log(Type.ERROR, "Exception reading resource", e);
			throw new UnableToCompleteException();
		}
	}

	public static URL getResourceUrl(TreeLogger logger, String path)
			throws UnableToCompleteException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(path);
		if(url == null) {
			logger.log(Type.ERROR, "Could not find resource");
			throw new UnableToCompleteException();
		}
		return url;
	}

	public static String getPathRelativeToPackage(JPackage pkg, String path) {
		return pkg.getName().replace('.', '/') + '/' + path;
	}
}
