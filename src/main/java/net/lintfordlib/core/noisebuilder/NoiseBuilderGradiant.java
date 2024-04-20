package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderGradiant extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mGx1, mGy1, mGz1;
	protected float mGx2, mGy2, mGz2;
	protected float mX, mY, mZ;
	protected float mLen;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderGradiant() {
		setGradient(0, 1, 0, 1, 0, 0);
	}

	public NoiseBuilderGradiant(float x1, float x2, float y1, float y2) {
		setGradient(x1, x2, y1, y2, 0, 0);
	}

	public NoiseBuilderGradiant(float x1, float x2, float y1, float y2, float z1, float z2) {
		setGradient(x1, x2, y1, y2, z1, z2);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setGradient(float x1, float x2, float y1, float y2, float z1, float z2) {
		mGx1 = x1;
		mGx2 = x2;
		mGy1 = y1;
		mGy2 = y2;
		mGz1 = z1;
		mGz2 = z2;

		mX = x2 - x1;
		mY = y2 - y1;
		mZ = z2 - z1;

		mLen = (mX * mX + mY * mY + mZ * mZ);
	}

	@Override
	public float get(float x, float y) {
		float dx = x - mGx1;
		float dy = y - mGy1;
		float dp = dx * mX + dy * mY;
		dp /= mLen;
		return dp;
	}

	@Override
	public float get(float x, float y, float z) {
		float dx = x - mGx1;
		float dy = y - mGy1;
		float dz = z - mGz1;
		float dp = dx * mX + dy * mY + dz * mZ;
		dp /= mLen;
		return dp;
	}

}