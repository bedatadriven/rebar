package com.bedatadriven.rebar.style.rebind.gss;

import com.bedatadriven.rebar.style.rebind.SourceResolver;
import com.bedatadriven.rebar.style.rebind.UserAgent;
import com.google.common.base.Predicates;
import com.google.common.css.IdentitySubstitutionMap;
import com.google.common.css.RecordingSubstitutionMap;
import com.google.common.css.Vendor;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.passes.*;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.util.Set;

public class GssTree {

  private CssTree cssTree;
  private Set<String> classNames;

  public GssTree(CssTree tree) {
    this.cssTree = tree;
    findClassNames();
  }

  private void findClassNames() {

    RecordingSubstitutionMap recordingSubstitutionMap =
        new RecordingSubstitutionMap(new IdentitySubstitutionMap(), Predicates.alwaysTrue());

    new CssClassRenaming(cssTree.getMutatingVisitController(), recordingSubstitutionMap, null)
        .runPass();

    this.classNames = recordingSubstitutionMap.getMappings().keySet();
  }

  public void eliminatedDeadStyles(TreeLogger logger) {

    LoggingErrorManager errorManager = new LoggingErrorManager(
        logger.branch(Type.INFO, "Optimizing CSS Tree"));

    // Report errors for duplicate declarations
    new DisallowDuplicateDeclarations(cssTree.getVisitController(), errorManager).runPass();
    // Split rules by selector and declaration.
    new SplitRulesetNodes(cssTree.getMutatingVisitController()).runPass();
    // Dead code elimination.
    new MarkRemovableRulesetNodes(cssTree).runPass();
    new EliminateUselessRulesetNodes(cssTree).runPass();
    // Merge of rules with same selector.
    new MergeAdjacentRulesetNodesWithSameSelector(cssTree).runPass();
    new EliminateUselessRulesetNodes(cssTree).runPass();
    // Merge of rules with same styles.
    new MergeAdjacentRulesetNodesWithSameDeclarations(cssTree).runPass();
    new EliminateUselessRulesetNodes(cssTree).runPass();
  }

  public void simplifyCSS() {
    // Eliminate empty rules.
    new EliminateEmptyRulesetNodes(cssTree.getMutatingVisitController()).runPass();
    // Eliminating units for zero values.
    new EliminateUnitsFromZeroNumericValues(cssTree.getMutatingVisitController()).runPass();
    // Optimize color values.
    new ColorValueOptimizer(cssTree.getMutatingVisitController()).runPass();
    // Compress redundant top-right-bottom-left value lists.
    new AbbreviatePositionalValues(cssTree.getMutatingVisitController()).runPass();
  }

  /**
   * @return the set of CSS class names used in the stylesheet
   */
  public Set<String> getClassNames() {
    return classNames;
  }

  public void stripVendorPrefixed(TreeLogger logger, UserAgent userAgent) throws UnableToCompleteException {

    new RemoveVendorSpecificProperties(vendorOf(userAgent),
        cssTree.getMutatingVisitController())
        .runPass();
  }

  private Vendor vendorOf(UserAgent userAgent) {
    switch (userAgent) {

      case GECKO1_8:
        return Vendor.MOZILLA;

      case SAFARI:
        return Vendor.WEBKIT;

      case IE7:
      case IE8:
      case IE9:
      case IE10:
        return Vendor.MICROSOFT;
    }
    throw new IllegalArgumentException("Unknown user agent: " + userAgent);
  }


  public void emitResources(TreeLogger logger, GeneratorContext context, SourceResolver resolver) throws UnableToCompleteException {
    EmitResources emitter = new EmitResources(cssTree.getMutatingVisitController(), context, logger, resolver);
    emitter.runPass();
    if (emitter.hasErrors()) {
      throw new UnableToCompleteException();
    }
  }


  public String toCompactCSS() {
    CompactPrinter printer = new CompactPrinter(cssTree);
    printer.runPass();
    return printer.getCompactPrintedString();
  }

}
