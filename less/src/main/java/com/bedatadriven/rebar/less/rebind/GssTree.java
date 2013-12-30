package com.bedatadriven.rebar.less.rebind;

import java.util.Map;
import java.util.Set;

import com.bedatadriven.rebar.less.rebind.passes.EmitFontResources;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.common.css.IdentitySubstitutionMap;
import com.google.common.css.MinimalSubstitutionMap;
import com.google.common.css.RecordingSubstitutionMap;
import com.google.common.css.SubstitutionMap;
import com.google.common.css.Vendor;
import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.passes.AbbreviatePositionalValues;
import com.google.common.css.compiler.passes.CollectConstantDefinitions;
import com.google.common.css.compiler.passes.CollectMixinDefinitions;
import com.google.common.css.compiler.passes.ColorValueOptimizer;
import com.google.common.css.compiler.passes.CompactPrinter;
import com.google.common.css.compiler.passes.CreateComponentNodes;
import com.google.common.css.compiler.passes.CreateConditionalNodes;
import com.google.common.css.compiler.passes.CreateConstantReferences;
import com.google.common.css.compiler.passes.CreateDefinitionNodes;
import com.google.common.css.compiler.passes.CreateMixins;
import com.google.common.css.compiler.passes.CreateStandardAtRuleNodes;
import com.google.common.css.compiler.passes.CssClassRenaming;
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
import com.google.common.css.compiler.passes.SplitRulesetNodes;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;

public class GssTree {

	private Set<String> allowedAtRules = Sets.newHashSet();
	private boolean simplifyCss = true;
	private boolean eliminateDeadStyles = false;
	private CssTree cssTree;
	private boolean obfuscateClassName = false;
	private Map<String, String> mappings;


	public GssTree(CssTree tree) {
		this.cssTree = tree;
	}

	public void finalizeTree(TreeLogger logger) throws UnableToCompleteException {
		//TODO
		// new CheckDependencyNodes(cssTree.getMutatingVisitController(), errorManager).runPass();

		LoggingErrorManager errorManager = new LoggingErrorManager(
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

	public void optimize(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {

		LoggingErrorManager errorManager = new LoggingErrorManager(
				logger.branch(Type.INFO, "Optimizing CSS Tree"));


		stripVendorPrefixed(context, logger);

		new ProcessComponents<Object>(cssTree.getMutatingVisitController(), errorManager).runPass();

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
	
	
	

	private void stripVendorPrefixed(GeneratorContext context, TreeLogger logger) throws UnableToCompleteException {
		
		String userAgent;
		try {
			userAgent = context.getPropertyOracle().getSelectionProperty(logger, "user.agent")
					.getCurrentValue();
		} catch (BadPropertyValueException e) {
			logger.log(Type.ERROR, "Exception getting user.agent property", e);
			throw new UnableToCompleteException();
		}
		
		if("safari".equals(userAgent)) {
			removeVendor(Vendor.WEBKIT);
		} else if("gecko1_8".equals(userAgent)) {
			removeVendor(Vendor.MOZILLA);
		} else if(userAgent.startsWith("ie")) {
			removeVendor(Vendor.MICROSOFT);
		}
	}

	private void removeVendor(Vendor vendor) {
		new RemoveVendorSpecificProperties(vendor, cssTree.getMutatingVisitController())
		.runPass();
	}

	public void renameClasses() {
		SubstitutionMap substitutionMap;
		if (obfuscateClassName) {
			// it renames CSS classes to the shortest string possible. No conflict possible
			substitutionMap = new MinimalSubstitutionMap();
		} else {
			// map the class name to itself (no renaming)
			substitutionMap = new IdentitySubstitutionMap();
		}

		// TODO : Do we have to rename differently the style class names for a GssResource used in two
		// different ClientBundle ?
//		String resourcePrefix = resourcePrefixBuilder.get(method.getReturnType()
//				.getQualifiedSourceName());
//		// This substitution map will prefix each renamed class with the resource prefix
//		SubstitutionMap prefixingSubstitutionMap = new PrefixingSubstitutionMap(substitutionMap,
//				resourcePrefix + "-");

		RecordingSubstitutionMap recordingSubstitutionMap =
				new RecordingSubstitutionMap(substitutionMap, Predicates.alwaysTrue());

		new CssClassRenaming(cssTree.getMutatingVisitController(), recordingSubstitutionMap, null)
		.runPass();

		this.mappings = recordingSubstitutionMap.getMappings();
	}

	public void emitResources(TreeLogger logger, GeneratorContext context) throws UnableToCompleteException {
		EmitFontResources emitter = new EmitFontResources(cssTree.getMutatingVisitController(), context, logger);
		emitter.runPass();
		if(emitter.hasErrors()) {
			throw new UnableToCompleteException();
		}
	}
	
	public Map<String, String> getMappings() {
		return mappings;
	}

	public String toCompactCSS() {
		CompactPrinter printer = new CompactPrinter(cssTree);
		printer.runPass();
		return printer.getCompactPrintedString();
	}

}
