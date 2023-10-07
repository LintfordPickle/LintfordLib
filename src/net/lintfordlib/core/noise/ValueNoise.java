package net.lintfordlib.core.noise;

import net.lintfordlib.core.maths.MathHelper;

public class ValueNoise {

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static float singleValue(int seed, float x, float y) {
		int x0 = MathHelper.floor(x);
		int y0 = MathHelper.floor(y);

		float xs = MathHelper.interpHermite((float) (x - x0));
		float ys = MathHelper.interpHermite((float) (y - y0));

		x0 *= NoiseHash.PrimeX;
		y0 *= NoiseHash.PrimeY;
		int x1 = x0 + NoiseHash.PrimeX;
		int y1 = y0 + NoiseHash.PrimeY;

		float xf0 = MathHelper.lerp(valCoord(seed, x0, y0), valCoord(seed, x1, y0), xs);
		float xf1 = MathHelper.lerp(valCoord(seed, x0, y1), valCoord(seed, x1, y1), xs);

		return MathHelper.lerp(xf0, xf1, ys);
	}

	public static float singleValue(int seed, float x, float y, float z) {
		int x0 = MathHelper.floor(x);
		int y0 = MathHelper.floor(y);
		int z0 = MathHelper.floor(z);

		float xs = MathHelper.interpHermite((float) (x - x0));
		float ys = MathHelper.interpHermite((float) (y - y0));
		float zs = MathHelper.interpHermite((float) (z - z0));

		x0 *= NoiseHash.PrimeX;
		y0 *= NoiseHash.PrimeY;
		z0 *= NoiseHash.PrimeZ;
		int x1 = x0 + NoiseHash.PrimeX;
		int y1 = y0 + NoiseHash.PrimeY;
		int z1 = z0 + NoiseHash.PrimeZ;

		float xf00 = MathHelper.lerp(valCoord(seed, x0, y0, z0), valCoord(seed, x1, y0, z0), xs);
		float xf10 = MathHelper.lerp(valCoord(seed, x0, y1, z0), valCoord(seed, x1, y1, z0), xs);
		float xf01 = MathHelper.lerp(valCoord(seed, x0, y0, z1), valCoord(seed, x1, y0, z1), xs);
		float xf11 = MathHelper.lerp(valCoord(seed, x0, y1, z1), valCoord(seed, x1, y1, z1), xs);

		float yf0 = MathHelper.lerp(xf00, xf10, ys);
		float yf1 = MathHelper.lerp(xf01, xf11, ys);

		return MathHelper.lerp(yf0, yf1, zs);
	}

	private static float valCoord(int seed, int xPrimed, int yPrimed) {
		int hash = NoiseHash.hash(seed, xPrimed, yPrimed);

		hash *= hash;
		hash ^= hash << 19;
		return hash * (1 / 2147483648.0f);
	}

	private static float valCoord(int seed, int xPrimed, int yPrimed, int zPrimed) {
		int hash = NoiseHash.hash(seed, xPrimed, yPrimed, zPrimed);

		hash *= hash;
		hash ^= hash << 19;
		return hash * (1 / 2147483648.0f);
	}

}
