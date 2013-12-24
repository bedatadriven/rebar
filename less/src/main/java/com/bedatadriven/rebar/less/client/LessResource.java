package com.bedatadriven.rebar.less.client;

import com.bedatadriven.rebar.less.rebind.LessResourceGenerator;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;

@DefaultExtensions(value = {".gss"})
@ResourceGeneratorType(LessResourceGenerator.class)
public interface LessResource extends CssResource {

}
