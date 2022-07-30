package net.lintford.library.core.noisebuilder;

public abstract class NoiseBuilderModuleBase {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected float mSpacing;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public void setDerivSpacing(float spacing) {
		mSpacing = spacing;
	};

	public void setSeed(int seed) {
	};

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public NoiseBuilderModuleBase() {
		mSpacing = 0.0001f;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public float get(float x, float y) {
		return 0;
	}

	public float get(float x, float y, float z) {
		return 0;
	}

	public float getDx(float x, float y) {
		return (get(x - mSpacing, y) - get(x + mSpacing, y)) / mSpacing;
	}

	public float getDy(float x, float y) {
		return (get(x, y - mSpacing) - get(x, y + mSpacing)) / mSpacing;
	}

	public float getDx(float x, float y, float z) {
		return (get(x - mSpacing, y, z) - get(x + mSpacing, y, z)) / mSpacing;
	}

	public float getDy(float x, float y, float z) {
		return (get(x, y - mSpacing, z) - get(x, y + mSpacing, z)) / mSpacing;
	}

	public float getDz(float x, float y, float z) {
		return (get(x, y, z - mSpacing) - get(x, y, z + mSpacing)) / mSpacing;
	}

}
