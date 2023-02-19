package net.lintford.library.core.noisebuilder;

import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.noise.Noise;
import net.lintford.library.core.noise.Noise.FractalType;
import net.lintford.library.core.noise.Noise.NoiseType;

public class NoiseBuilderFractal extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mSeed;
	protected boolean mRotatedDomain;
	private final Matrix4f mRotMatix = new Matrix4f();
	protected final Noise mNoise = new Noise();
	protected float mCos = 1;
	protected float mSin = 0;

	protected NoiseType mNoiseType = NoiseType.OpenSimplex2S;
	protected FractalType mFractalType = FractalType.None;
	protected int mOctaves = 6;
	protected float mFrequency = 0.01f;
	protected float mLacunarity = 2.0f;;
	protected float mGain = .5f;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderFractal(int seed, NoiseType noiseType, FractalType fractalType, int octaves, float frequency) {
		mSeed = seed;
		mNoiseType = noiseType;
		mFractalType = fractalType;
		mFrequency = frequency;
		mOctaves = octaves;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setNoiseType(NoiseType t) {
		if (t == null) {
			mNoiseType = NoiseType.OpenSimplex2S;
			return;
		}
		mNoiseType = t;
	}

	public void setFrequency(float v) {
		mFrequency = v;
	}

	public void setLacunarity(float v) {
		mLacunarity = v;
	}

	public void setGain(float v) {
		mGain = v;
	}

	public void setSeed(int v) {
		mSeed = v;
		mNoise.setSeed(mSeed);
	}

	public void setRotatedDomain(boolean v) {
		mRotatedDomain = v;
	}

	public void setNoRotation() {
		mRotatedDomain = false;
		mRotMatix.setIdentity();
		mCos = 1.f;
		mSin = 0.f;
	}

	public void setRotationAngle(float x, float y, float z, float angle) {
		mRotatedDomain = true;

		mCos = (float) Math.cos(angle);
		mSin = (float) Math.sin(angle);

		mRotMatix.m00 = 1 + (1 - mCos) * (x * x - 1);
		mRotMatix.m10 = -z * mSin + (1 - mCos) * x * y;
		mRotMatix.m20 = y * mSin + (1 - mCos) * x * z;

		mRotMatix.m01 = z * mSin + (1 - mCos) * x * y;
		mRotMatix.m11 = 1 + (1 - mCos) * (y * y - 1);
		mRotMatix.m21 = -x * mSin + (1 - mCos) * y * z;

		mRotMatix.m02 = -y * mSin + (1 - mCos) * x * z;
		mRotMatix.m12 = x * mSin + (1 - mCos) * y * z;
		mRotMatix.m22 = 1 + (1 - mCos) * (z * z - 1);
	}

	@Override
	public float get(float x, float y) {
		final float nx = x * mCos - y * mSin;
		final float ny = y * mCos + x * mSin;

		mNoise.setNoiseType(mNoiseType);
		mNoise.setFractalType(mFractalType);
		mNoise.setSeed(mSeed);
		mNoise.setFractalOctaves(mOctaves);
		mNoise.setFrequency(mFrequency);
		// mNoise.setFractalGain(mGain);

		float result = mNoise.getNoise(nx, ny);
		return result;
	}

	@Override
	public float get(float x, float y, float z) {
		final float nx = (mRotMatix.m00 * x) + (mRotMatix.m10 * y) + (mRotMatix.m20 * z);
		final float ny = (mRotMatix.m01 * x) + (mRotMatix.m11 * y) + (mRotMatix.m21 * z);
		final float nz = (mRotMatix.m02 * x) + (mRotMatix.m12 * y) + (mRotMatix.m22 * z);

		return mNoise.getNoise(nx, ny, nz);
	}
}
