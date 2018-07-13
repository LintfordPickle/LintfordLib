package net.lintford.library.core.fractal;

import net.lintford.library.core.maths.RandomNumbers;

public class LRuleSet {
	public String left;
	public String[] right;

	public LRuleSet(String pLeft, String[] pRight) {
		left = pLeft;
		right = pRight;

	}

	public String processRule(String pIn) {
		if (right == null || right.length == 0)
			return pIn;

		final int randIndex = RandomNumbers.random(0, right.length);
		String lStr = pIn.replaceAll(left, right[randIndex]);

		return lStr;
	}

}
