package net.lintfordlib.core.noise;

public class NoiseHash {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	static final int PrimeX = 501125321;
	static final int PrimeY = 1136930381;
	static final int PrimeZ = 1720413743;

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	static int hash(int seed, int xPrimed, int yPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed;

		hash *= 0x27d4eb2d;
		return hash;
	}

	static int hash(int seed, int xPrimed, int yPrimed, int zPrimed) {
		int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed;

		hash *= 0x27d4eb2d;
		return hash;
	}

	static float valCoord(int seed, int xPrimed, int yPrimed) {
		int hash = hash(seed, xPrimed, yPrimed);

		hash *= hash;
		hash ^= hash << 19;
		return hash * (1 / 2147483648.0f);
	}

	static float valCoord(int seed, int xPrimed, int yPrimed, int zPrimed) {
		int hash = hash(seed, xPrimed, yPrimed, zPrimed);

		hash *= hash;
		hash ^= hash << 19;
		return hash * (1 / 2147483648.0f);
	}

}
