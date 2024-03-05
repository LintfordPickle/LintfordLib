package net.lintfordlib.core.maths;

import java.util.SplittableRandom;

public class RandomNumbers {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static SplittableRandom RANDOM = new SplittableRandom();

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static int nextInt() {
		return RANDOM.nextInt();
	}

	public static long nextLong() {
		return RANDOM.nextLong();
	}

	public static double nextDouble() {
		return RANDOM.nextDouble();
	}

	public static float nextFloat() {
		return (RANDOM.nextInt() >>> 8) * 5.960464477539063E-8f;
	}

	public static void reseed() {
		RANDOM = new SplittableRandom();
	}
	
	public static void reseed(long seed) {
		RANDOM = new SplittableRandom(seed);
	}

	public static final int randomSign() {
		if (RANDOM.nextBoolean()) {
			return 1;
		} else {
			return -1;
		}
	}

	public static final float random(float minValue, float maxValue) {
		return minValue + (float) RANDOM.nextDouble() * (maxValue - minValue);
	}

	public static final int random(int minValue, int maxValue) {
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
