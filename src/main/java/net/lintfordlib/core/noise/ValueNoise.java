package net.lintfordlib.core.noise;

import net.lintfordlib.core.maths.InterpolationHelper;
import net.lintfordlib.core.maths.MathHelper;

public class ValueNoise {

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static float singleValue(int seed, float x, float y) {
		int x0 = MathHelper.floor(x);
		int y0 = MathHelper.floor(y);

		final var xs = InterpolationHelper.hermite((float) (x - x0));
		final var ys = InterpolationHelper.hermite((float) (y - y0));

		x0 *= NoiseHash.PrimeX;
		y0 *= NoiseHash.PrimeY;
		final var x1 = x0 + NoiseHash.PrimeX;
		final var y1 = y0 + NoiseHash.PrimeY;

		final var xf0 = InterpolationHelper.lerp(valCoord(seed, x0, y0), valCoord(seed, x1, y0), xs);
		final var xf1 = InterpolationHelper.lerp(valCoord(seed, x0, y1), valCoord(seed, x1, y1), xs);

		return InterpolationHelper.lerp(xf0, xf1, ys);
	}

	public static float singleValue(int seed, float x, float y, float z) {
		int x0 = MathHelper.floor(x);
		int y0 = MathHelper.floor(y);
		int z0 = MathHelper.floor(z);

		float xs = InterpolationHelper.hermite((float) (x - x0));
		float ys = InterpolationHelper.hermite((float) (y - y0));
		float zs = InterpolationHelper.hermite((float) (z - z0));

		x0 *= NoiseHash.PrimeX;
		y0 *= NoiseHash.PrimeY;
		z0 *= NoiseHash.PrimeZ;
		final var x1 = x0 + NoiseHash.PrimeX;
		final var y1 = y0 + NoiseHash.PrimeY;
		final var z1 = z0 + NoiseHash.PrimeZ;

		final var xf00 = InterpolationHelper.lerp(valCoord(seed, x0, y0, z0), valCoord(seed, x1, y0, z0), xs);
		final var xf10 = InterpolationHelper.lerp(valCoord(seed, x0, y1, z0), valCoord(seed, x1, y1, z0), xs);
		final var xf01 = InterpolationHelper.lerp(valCoord(seed, x0, y0, z1), valCoord(seed, x1, y0, z1), xs);
		final var xf11 = InterpolationHelper.lerp(valCoord(seed, x0, y1, z1), valCoord(seed, x1, y1, z1), xs);

		final var yf0 = InterpolationHelper.lerp(xf00, xf10, ys);
		final var yf1 = InterpolationHelper.lerp(xf01, xf11, ys);

		return InterpolationHelper.lerp(yf0, yf1, zs);
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
