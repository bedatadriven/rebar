package com.bedatadriven.rebar.style.link;

import java.util.Set;

import com.google.common.css.compiler.ast.CssRulesetNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;

public class PruningUnusedClasses extends DefaultTreeVisitor {
	
	private Set<String> classesToKeep;
	private MutatingVisitController visitController;

	public PruningUnusedClasses(Set<String> classesToKeep,
			MutatingVisitController visitController) {
		this.classesToKeep = classesToKeep;
		this.visitController = visitController;
	}

	

	@Override
	public boolean enterRuleset(CssRulesetNode ruleset) {
//		for(CssSelectorNode selector : ruleset.getSelectors().getChildren()) {
//			selector.getCombinator()
//		}
		return true;
	}



	public void runPass() {
		visitController.startVisit(this);
	}
}