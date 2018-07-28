package net.lintford.library.core.fractal;

public class LSystem {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LSystem() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public String getLSystemString(LSystemDefinition pLSystemDef, int pIterations) {
		String lCurrentString = pLSystemDef.axiom;
		for (int i = 0; i < pIterations; i++) {
			final int RULESET_SIZE = pLSystemDef.rules.size();
			for (int j = 0; j < RULESET_SIZE; j++) {
				lCurrentString = pLSystemDef.rules.get(j).processRule(lCurrentString);

			}

		}

		return lCurrentString;

	}

}
