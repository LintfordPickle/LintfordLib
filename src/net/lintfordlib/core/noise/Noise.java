package net.lintfordlib.core.noise;

import net.lintfordlib.core.maths.MathHelper;

public class Noise {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public enum NoiseType {
		OpenSimplex2, OpenSimplex2S, Cellular, Value
	};

	public enum CellularDistanceFunction {
		Euclidean, EuclideanSq, Manhattan, Hybrid
	};

	public enum CellularReturnType {
		CellValue, Distance, Distance2, Distance2Add, Distance2Sub, Distance2Mul, Distance2Div
	};

	public enum FractalType {
		None, FBm, Ridged, Billow, PingPong, DomainWarpProgressive, DomainWarpIndependent
	};

	public enum DomainWarpType {
		OpenSimplex2, OpenSimplex2Reduced, BasicGrid
	};

	public enum RotationType3D {
		None, ImproveXYPlanes, ImproveXZPlanes
	};

	public enum TransformType3D {
		None, ImproveXYPlanes, ImproveXZPlanes, DefaultOpenSimplex2
	};

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mSeed = 1337;
	private float mFrequency = 0.01f;
	private NoiseType mNoiseType = NoiseType.OpenSimplex2;
	private TransformType3D mTransformType3D = TransformType3D.DefaultOpenSimplex2;

	private FractalType mFractalType = FractalType.None;
	private int mOctaves = 3;
	private float mLacunarity = 2.0f;
	private float mGain = 0.5f;
	private float mWeightedStrength = 0.0f;
	private float mPingPongStrength = 2.0f;

	private CellularDistanceFunction mCellularDistanceFunction = CellularDistanceFunction.EuclideanSq;
	private CellularReturnType mCellularReturnType = CellularReturnType.Distance;
	private float mCellularJitterModifier = 1.0f;

	private float mFractalBounding = 1 / 1.75f;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public void setSeed(int seed) {
		mSeed = seed;
	}

	public void setNoiseType(NoiseType noiseType) {
		mNoiseType = noiseType;
	}

	public void setFrequency(float frequency) {
		mFrequency = frequency;
	}

	public void setFractalType(FractalType fractalType) {
		mFractalType = fractalType;
	}

	public void setFractalLacunarity(float lacunarity) {
		mLacunarity = lacunarity;
	}

	public void setFractalOctaves(int octaves) {
		mOctaves = octaves;
		calculateFractalBounding();
	}

	public void setFractalGain(float gain) {
		mGain = gain;
		calculateFractalBounding();
	}

	/**
	 * Sets octave weighting for all none DomainWarp fratal types
	 * @apiNote Keep between 0...1 to maintain -1...1 output bounding
	 * */
	public void setFractalWeightedStrength(float weightedStrength) {
		mWeightedStrength = weightedStrength;
	}

	/**
	 * Sets strength of the fractal ping pong effect
	 * Default: 2.0
	 * */
	public void setFractalPingPongStrength(float pingPongStrength) {
		mPingPongStrength = pingPongStrength;
	}

	public void setCellularDistanceFunction(CellularDistanceFunction cellularDistanceFunction) {
		mCellularDistanceFunction = cellularDistanceFunction;
	}

	public void setCellularReturnType(CellularReturnType cellularReturnType) {
		mCellularReturnType = cellularReturnType;
	}

	public void setCellularJitter(float cellularJitter) {
		mCellularJitterModifier = cellularJitter;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public Noise() {
	}

	public Noise(int seed) {
		setSeed(seed);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public float getNoise(float x, float y) {
		x *= mFrequency;
		y *= mFrequency;

		switch (mNoiseType) {
		case OpenSimplex2:
		case OpenSimplex2S: {
			final float SQRT3 = (float) 1.7320508075688772935274463415059;
			final float F2 = 0.5f * (SQRT3 - 1);
			float t = (x + y) * F2;
			x += t;
			y += t;
		}
			break;
		default:
			break;
		}

		switch (mFractalType) {
		default:
			return genNoiseSingle(mSeed, x, y);
		case FBm:
			return genFractalFBm(x, y);
		case Billow:
			return genFractelBillow(x, y);
		case Ridged:
			return genFractalRidged(x, y);
		case PingPong:
			return genFractalPingPong(x, y);
		}
	}

	public float getNoise(float x, float y, float z) {
		x *= mFrequency;
		y *= mFrequency;
		z *= mFrequency;

		switch (mTransformType3D) {
		case ImproveXYPlanes: {
			float xy = x + y;
			float s2 = xy * -(float) 0.211324865405187;
			z *= (float) 0.577350269189626;
			x += s2 - z;
			y = y + s2 - z;
			z += xy * (float) 0.577350269189626;
		}
			break;
		case ImproveXZPlanes: {
			float xz = x + z;
			float s2 = xz * -(float) 0.211324865405187;
			y *= (float) 0.577350269189626;
			x += s2 - y;
			z += s2 - y;
			y += xz * (float) 0.577350269189626;
		}
			break;
		case DefaultOpenSimplex2: {
			final float R3 = (float) (2.0 / 3.0);
			final float r = (x + y + z) * R3;
			x = r - x;
			y = r - y;
			z = r - z;
		}
			break;
		default:
			break;
		}

		switch (mFractalType) {
		default:
			return genNoiseSingle(mSeed, x, y, z);
		case FBm:
			return genFractalFBm(x, y, z);
		case Ridged:
			return genFractalRidged(x, y, z);
		case PingPong:
			return genFractalPingPong(x, y, z);
		}
	}

	private float genNoiseSingle(int seed, float x, float y) {
		switch (mNoiseType) {
		case OpenSimplex2:
			return OpenSimplex2.noise2(seed, x, y);
		case OpenSimplex2S:
			return OpenSimplex2S.noise2(seed, x, y);
		case Cellular:
			return CellularNoise.singleCellular(seed, x, y, mCellularJitterModifier, mCellularDistanceFunction, mCellularReturnType);
		case Value:
			return ValueNoise.singleValue(seed, x, y);
		default:
			return 0;
		}
	}

	private float genNoiseSingle(int seed, float x, float y, float z) {
		switch (mNoiseType) {
		case OpenSimplex2:
			return OpenSimplex2.noise3_Fallback(seed, x, y, z);
		case OpenSimplex2S:
			return OpenSimplex2S.noise3_Fallback(seed, x, y, z);
		case Cellular:
			return CellularNoise.singleCellular(seed, x, y, z, mCellularJitterModifier, mCellularDistanceFunction, mCellularReturnType);
		case Value:
			return ValueNoise.singleValue(seed, x, y, z);
		default:
			return 0;
		}
	}

	// --------------------------------

	// Fractal FBm

	private float genFractalFBm(float x, float y) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = genNoiseSingle(seed++, x, y);
			sum += noise * amp;
			amp *= MathHelper.lerp(1.0f, MathHelper.min(noise + 1, 2) * 0.5f, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	private float genFractalFBm(float x, float y, float z) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = genNoiseSingle(seed++, x, y, z);
			sum += noise * amp;
			amp *= MathHelper.lerp(1.0f, (noise + 1) * 0.5f, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			z *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	// Fractal Billow

	private float genFractelBillow(float x, float y) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = genNoiseSingle(seed++, x, y);
			noise = Math.abs(noise) * 2.f - 1f;

			sum += noise * amp;
			amp *= MathHelper.lerp(1.0f, MathHelper.min(noise + 1, 2) * 0.5f, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	// Fractal Ridged

	private float genFractalRidged(float x, float y) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = MathHelper.abs(genNoiseSingle(seed++, x, y));
			sum += (noise * -2 + 1) * amp;
			amp *= MathHelper.lerp(1.0f, 1 - noise, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	private float genFractalRidged(float x, float y, float z) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = MathHelper.abs(genNoiseSingle(seed++, x, y, z));
			sum += (noise * -2 + 1) * amp;
			amp *= MathHelper.lerp(1.0f, 1 - noise, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			z *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	// Fractal PingPong 

	private float genFractalPingPong(float x, float y) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = MathHelper.pingPong((genNoiseSingle(seed++, x, y) + 1) * mPingPongStrength);
			sum += (noise - 0.5f) * 2 * amp;
			amp *= MathHelper.lerp(1.0f, noise, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	private float genFractalPingPong(float x, float y, float z) {
		int seed = mSeed;
		float sum = 0;
		float amp = mFractalBounding;

		for (int i = 0; i < mOctaves; i++) {
			float noise = MathHelper.pingPong((genNoiseSingle(seed++, x, y, z) + 1) * mPingPongStrength);
			sum += (noise - 0.5f) * 2 * amp;
			amp *= MathHelper.lerp(1.0f, noise, mWeightedStrength);

			x *= mLacunarity;
			y *= mLacunarity;
			z *= mLacunarity;
			amp *= mGain;
		}

		return sum;
	}

	// --------------------------------

	private void calculateFractalBounding() {
		float gain = MathHelper.abs(mGain);
		float amp = gain;
		float ampFractal = 1.0f;
		for (int i = 1; i < mOctaves; i++) {
			ampFractal += amp;
			amp *= gain;
		}
		mFractalBounding = 1 / ampFractal;
	}
}
