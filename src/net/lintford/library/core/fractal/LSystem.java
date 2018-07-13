package net.lintford.library.core.fractal;

import java.util.ArrayList;
import java.util.List;

public class LSystem {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String axiom;
	public List<LRuleSet> rules = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LSystem() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public String getLSystemString(int pIterations) {
		String lCurrentString = axiom;
		for (int i = 0; i < pIterations; i++) {
			final int RULESET_SIZE = rules.size();
			for (int j = 0; j < RULESET_SIZE; j++) {
				lCurrentString = rules.get(j).processRule(lCurrentString);

			}

		}

		return lCurrentString;

	}

}
