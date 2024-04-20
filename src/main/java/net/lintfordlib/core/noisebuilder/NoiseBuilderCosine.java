package net.lintfordlib.core.noisebuilder;

public class NoiseBuilderCosine extends NoiseBuilderModuleBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mFrequency = 0.01f;
	protected float mAmplitude = 2.0f;;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public NoiseBuilderCosine(float frequency, float amplitude) {
		mFrequency = frequency;
		mAmplitude = amplitude;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setFrequency(float v) {
		mFrequency = v;
	}

	public void setAmplitude(float v) {
		mAmplitude = v;
	}

	@Override
	public float get(float x, float y) {
		return (float) (Math.cos(x * mFrequency)) * mAmplitude;
	}

	@Override
	public float get(float x, float y, float z) {
		return (float) Math.cos(x * mFrequency) * mAmplitude;
	}
}
