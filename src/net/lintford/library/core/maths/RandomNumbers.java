package net.lintford.library.core.maths;

import java.util.Random;

public class RandomNumbers {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static Random RANDOM = new Random();

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static void reseed() {
		RANDOM = new Random();
	}

	public static final int randomSign() {
		if (RANDOM.nextBoolean()) {
			return 1;

		} else {
			return -1;

		}

	}

	public static final float random(final float pMin, final float pMax) {
		return pMin + RANDOM.nextFloat() * (pMax - pMin);

	}

	public static final int random(final int pMin, final int pMax) {
		return pMin + RANDOM.nextInt(pMax - pMin);
	}

	/**
	 * Returns a random true/false with the given percentage chance of being true.
	 * 
	 * @param pPercentChance a value between 0-100
	 * @return true or false
	 */
	public static final boolean getRandomChance(float pPercentChance) {
		if (pPercentChance < 0)
			pPercentChance = 0;
		if (pPercentChance > 100)
			pPercentChance = 100;

		return RANDOM.nextInt(100) < pPercentChance;
	}

}
