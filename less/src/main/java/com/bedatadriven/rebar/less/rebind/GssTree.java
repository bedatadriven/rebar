package com.bedatadriven.rebar.less.rebind;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.css.Vendor;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssFunction;
import com.google.common.css.compiler.passes.AbbreviatePositionalValues;
import com.google.common.css.compiler.passes.CollectConstantDefinitions;
import com.google.common.css.compiler.passes.CollectMixinDefinitions;
import com.google.common.css.compiler.passes.ColorValueOptimizer;
import com.google.common.css.compiler.passes.CreateComponentNodes;
import com.google.common.css.compiler.passes.CreateConditionalNodes;
import com.google.common.css.compiler.passes.CreateConstantReferences;
import com.google.common.css.compiler.passes.CreateDefinitionNodes;
import com.google.common.css.compiler.passes.CreateMixins;
import com.google.common.css.compiler.passes.CreateStandardAtRuleNodes;
import com.google.common.css.compiler.passes.DisallowDuplicateDeclarations;
import com.google.common.css.compiler.passes.EliminateConditionalNodes;
import com.google.common.css.compiler.passes.EliminateEmptyRulesetNodes;
import com.google.common.css.compiler.passes.EliminateUnitsFromZeroNumericValues;
import com.google.common.css.compiler.passes.EliminateUselessRulesetNodes;
import com.google.common.css.compiler.passes.HandleUnknownAtRuleNodes;
import com.google.common.css.compiler.passes.MarkRemovableRulesetNodes;
import com.google.common.css.compiler.passes.MergeAdjacentRulesetNodesWithSameDeclarations;
import com.google.common.css.compiler.passes.MergeAdjacentRulesetNodesWithSameSelector;
import com.google.common.css.compiler.passes.ProcessComponents;
import com.google.common.css.compiler.passes.ProcessKeyframes;
import com.google.common.css.compiler.passes.ProcessRefiners;
import com.google.common.css.compiler.passes.RemoveVendorSpecificProperties;
import com.google.common.css.compiler.passes.ReplaceConstantReferences;
import com.google.common.css.compiler.passes.ReplaceMixins;
import com.google.common.css.compiler.passes.ResolveCustomFunctionNodes;
import com.google.common.css.compiler.passes.SplitRulesetNodes;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.resources.gss.CssPrinter;
import com.google.gwt.resources.gss.ExternalClassesCollector;
import com.google.gwt.resources.gss.GwtGssFunctionMapProvider;

public class GssTree {

	private Set<String> allowedAtRules = Sets.newHashSet();
	private boolean simplifyCss = true;
	private boolean eliminateDeadStyles = false;
	private Set<String> allowedNonStandardFunctions = Sets.newHashSet();
	private Set<String> externalClassNames;
	private CssTree cssTree;

	
	public GssTree(CssTree tree) {
		this.cssTree = tree;
	}

	public void finalizeTree(TreeLogger logger) throws UnableToCompleteException {
		//TODO
		// new CheckDependencyNodes(cssTree.getMutatingVisitController(), errorManager).runPass();

		LoggerErrorManager errorManager = new LoggerErrorManager(
				logger.branch(Type.INFO, "Finalizing Closure Stylesheet"));

		new CreateStandardAtRuleNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
		new CreateMixins(cssTree.getMutatingVisitController(), errorManager).runPass();
		new CreateDefinitionNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
		new CreateConstantReferences(cssTree.getMutatingVisitController()).runPass();
		new CreateConditionalNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
		new CreateComponentNodes(cssTree.getMutatingVisitController(), errorManager).runPass();

		new HandleUnknownAtRuleNodes(cssTree.getMutatingVisitController(), errorManager,
				allowedAtRules , true, false).runPass();
		new ProcessKeyframes(cssTree.getMutatingVisitController(), errorManager, true, true).runPass();
		new ProcessRefiners(cssTree.getMutatingVisitController(), errorManager, true).runPass();
	}

	public void optimize(TreeLogger logger, String userAgent) {

		LoggerErrorManager errorManager = new LoggerErrorManager(
				logger.branch(Type.INFO, "Optimizing CSS Tree"));

		
		if("safari".equals(userAgent)) {
			removeVendor(Vendor.WEBKIT);
		} else if("gecko1_8".equals(userAgent)) {
			removeVendor(Vendor.MOZILLA);
		} else if(userAgent.startsWith("ie")) {
			removeVendor(Vendor.MICROSOFT);
		}
		
		// Collect mixin definitions and replace mixins
		CollectMixinDefinitions collectMixinDefinitions = new CollectMixinDefinitions(
				cssTree.getMutatingVisitController(), errorManager);
		collectMixinDefinitions.runPass();
		new ReplaceMixins(cssTree.getMutatingVisitController(), errorManager,
				collectMixinDefinitions.getDefinitions()).runPass();

		ExternalClassesCollector externalClassesCollector = new ExternalClassesCollector(cssTree
				.getMutatingVisitController(), errorManager);
		externalClassesCollector.runPass();
		this.externalClassNames = externalClassesCollector.getExternalClassNames();

		new ProcessComponents<Object>(cssTree.getMutatingVisitController(), errorManager).runPass();

		// TODO pass true conditions
		new EliminateConditionalNodes(cssTree.getMutatingVisitController(),
				Sets.<String>newHashSet()).runPass();

		CollectConstantDefinitions collectConstantDefinitionsPass = new CollectConstantDefinitions(
				cssTree);
		collectConstantDefinitionsPass.runPass();

		ReplaceConstantReferences replaceConstantReferences = new ReplaceConstantReferences(cssTree,
				collectConstantDefinitionsPass.getConstantDefinitions(), true, errorManager, false);
		replaceConstantReferences.runPass();

		Map<String, GssFunction> gssFunctionMap = new GwtGssFunctionMapProvider().get();
		new ResolveCustomFunctionNodes(cssTree.getMutatingVisitController(), errorManager,
				gssFunctionMap, true, allowedNonStandardFunctions).runPass();

		// will be done at LESS level if at all
		//new ImageSpriteCreator(cssTree.getMutatingVisitController(), context, errorManager).runPass();

		if (simplifyCss) {
			// Eliminate empty rules.
			new EliminateEmptyRulesetNodes(cssTree.getMutatingVisitController()).runPass();
			// Eliminating units for zero values.
			new EliminateUnitsFromZeroNumericValues(cssTree.getMutatingVisitController()).runPass();
			// Optimize color values.
			new ColorValueOptimizer(cssTree.getMutatingVisitController()).runPass();
			// Compress redundant top-right-bottom-left value lists.
			new AbbreviatePositionalValues(cssTree.getMutatingVisitController()).runPass();
		}

		if (eliminateDeadStyles) {
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

	}

	private void removeVendor(Vendor vendor) {
		new RemoveVendorSpecificProperties(vendor, cssTree.getMutatingVisitController())
			.runPass();
	}

	public String toCompactCSS() {
		CssPrinter cssPrinterPass = new CssPrinter(cssTree);
		cssPrinterPass.runPass();

		return cssPrinterPass.getCompactPrintedString();
	}

}
