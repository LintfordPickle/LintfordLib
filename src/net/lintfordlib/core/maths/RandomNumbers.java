package net.lintfordlib.core.maths;

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

	public static final float random(final float minValue, final float maxValue) {
		return minValue + RANDOM.nextFloat() * (maxValue - minValue);
	}

	public static final int random(final int minValue, final int maxValue) {
		if (maxValue <= 0)
			return 0;

		return minValue + RANDOM.nextInt(maxValue - minValue);
	}

	/**
	 * Returns a random true/false with the given percentage chance of being true.
	 * 
	 * @param percentChance a value between 0-100
	 * @return true or false
	 */
	public static final boolean getRandomChance(float percentChance) {
		if (percentChance < 0)
			percentChance = 0;
		if (percentChance > 100)
			percentChance = 100;

		return RANDOM.nextInt(100) < percentChance;
	}
}
