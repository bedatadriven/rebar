package com.bedatadriven.rebar.style.client;

import com.google.gwt.resources.client.CssResource;


/**
 * Marker interface for CSS/LESS Stylesheets. At compile time, the 
 * referenced {@link Source}s will be concatenated and compiled with the 
 * Less Compiler (version 1.4.0).
 * 
 * <p>For example:
 * 
 * <pre>
 * @Source("grid.less")
 * @Source("forms.less")
 * public interface MyStyle extends Stylesheet {
 * 
 *   @ClassName("col-md-12")
 *   String colMd12();
 * 
 * }
 * </pre>
 * 
 * <p>At link time, the resulting CSS will be combined with all other {@code Stylesheet}s,
 * pruned, and minified. 
 * 
 * <p>CSS class names should only be referenced in your application using the methods of your
 * Stylesheet interface; this allows the StylesheetLinker to rename (compact) class names
 * and determine which CSS rule sets are not used and can be safely pruned.</p>
 * 
 * <h3>Global Imports</h3>
 * 
 * <p>In addition to the files referenced by @Source, application-wide LESS variables and mixins can also 
 * be specified (and overriden) using the {@code less.global.import} configuration properties. 
 * For example, you could add the following directives to your MyApp.gwt.xml module descriptor to provide a set of
 * variables and mixins to all {@code Stylesheets} in your application:
 * 
 * <pre>
 *   &lt;extend-configuration-property name="less.global.import" 
 *           value="com/mycompany/myapp/client/variables.less" /&gt;
 *   &lt;extend-configuration-property name="less.global.import" 
 *           value="com/mycompany/myapp/client/mixins.less" /&gt;
 * </pre>
 * 
 * <p>Applications can, for example, define a {@code less.global.import} to override Bootstrap's 
 * default variables included from another library because when LESS 
 * variables are defined twice, the last definition of the variable is used, even if 
 * the definition occurs after its use in a rule set.
 * 
 * <h3>Deferred-binding properties in LESS</h3>
 * 
 * <p>The {@code StylesheetGenerator} also provides LESS variables corresponding to GWT
 * deferred binding properties, such as {@code user.agent}, allowing you to tailor
 * your style sheets on a per user-agent basis. You can for example, define:
 * 
 * <pre>
 * .bg() when (@gwt-user-agent = 'ie7'), (@gwt-user-agent = 'ie8') {
 *	 background-color: #000; 	
 * }
 * .bg() when (@gwt-user-agent = 'ie9') {
 *   background-color: rgba(0,0,0,0);
 * }
 * .bg() { // default
 * }</pre>
 * 
 * <p>At compile time, a version of the {@code Stylesheet} will be compiled for 
 * each {@code user.agent}, including only the CSS relevant for a given
 * permutation.
 * </p>
 * 
 * <h3>Font-Faces</h3>
 * 
 * <p>You can reference web fonts in your LESS/CSS using a url() pointing to a
 * resource on the classpath. (Currently the url must be absolute) 
 * 
 * <pre>
 * font-face {
 *   font-family: 'Glyphicons Halflings';
 *    src: url('com/mycompany/myapp/client/glyphicons-halflings-regular.ttf');
 * }
 * </pre>
 * 
 * <p>Rebar's {@code StylesheetGenerator} will convert the font to an appropriate format
 * for each user.agent, and include the result in your module's output
 * as a strongly-named resource, for example, as "39284325F99CD3838170721A73810E06.cache.woff"
 * 
 * <h3>Multiple Stylesheet</h3>
 * 
 * <p>You can define multiple {@code Stylesheet} interfaces in your application, or include
 * others {@code Stylesheet}s packaged as libraries. The following rules hold:
 * <ul>
 * <li>Each {@code Stylesheet} is compiled independently with the LESS compiler. The resulting CSS is then combined 
 * together at the end of compilation.
 * <li>This means that LESS variables defined in 
 * other {@code Stylesheet}s, but you <em>can</em> reference other CSS classes by name: all Stylesheets
 * share a common CSS class namespace. If you need to share LESS variable or mixin definitions
 * between multiple Stylesheets, put them in a seperate file and reference them using the 
 * {@code less.global.import} configuration property.
 * </ul>
 * 
 * <h3>Differences with ClientBundle and CSSResource</h3>
 *  
 * <p>{@code Stylesheet} inherits from the {@link CssResource} in order to interoperate
 * with UI Binder, but there are a number of important differences between 
 * Rebar's {@code StylesheetGenerator} and GWT's {@code ClientBundle} architecture:
 * 
 * <ul>
 * <li>Sources are associated with a Stylesheet interface, not with a method in a
 * ClientBundle. This means that a given Stylesheet interface always refers to the 
 * same LESS.
 * <li>CSS class names are global; a {@code .widget} class defined in {@code MyStylesheet} and 
 * {@code .widget} class defined in {@code TheirStylesheet} will collide.
 * <li>Image sprites are not supported; consider using icon fonts instead.
 * </ul>
 *  
 * @see <a href="http://lesscss.org">LESS Documentation</a>
 */
public interface Stylesheet extends CssResource {

}
