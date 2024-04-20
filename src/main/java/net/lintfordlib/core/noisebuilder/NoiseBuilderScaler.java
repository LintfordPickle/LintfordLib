package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderScaler {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected float mValue;
	protected NoiseBuilderModuleBase mSource;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public void set(float value) {
		mValue = value;
	}

	public void set(NoiseBuilderModuleBase b) {
		mSource = b;
	}

	public float get(float x, float y) {
		if (mSource != null)
			return mSource.get(x, y);
		return mValue;
	}

	public float get(float x, float y, float z) {
		if (mSource != null)
			return mSource.get(x, y, z);
		return mValue;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public NoiseBuilderScaler(float value) {
		mValue = value;
		mSource = null;
	}

	public NoiseBuilderScaler(NoiseBuilderModuleBase b) {
		mValue = 0;
		mSource = b;
	}

	public NoiseBuilderScaler(NoiseBuilderScaler p) {
		mValue = p.mValue;
		mSource = p.mSource;
	}

}
