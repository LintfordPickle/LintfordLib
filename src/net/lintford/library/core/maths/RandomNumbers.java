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
		return pMin + RANDOM.nextInt(pMax - pMin + 1);
	}

}
