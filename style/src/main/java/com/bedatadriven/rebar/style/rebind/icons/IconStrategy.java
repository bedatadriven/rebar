package com.bedatadriven.rebar.style.rebind.icons;

import com.google.gwt.core.ext.TreeLogger;

import java.util.List;

/**
 * Composes the CSS rule for an icon using the best strategy for the given browser
 */
public interface IconStrategy {

  String getName();

  IconArtifacts execute(TreeLogger logger, IconContext context, List<Icon> icons);

}
