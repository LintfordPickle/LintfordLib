package net.lintfordlib.core.audio;

import net.lintfordlib.core.maths.InterpolationHelper;

public class ParamFade {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum FadeCurve {
		LINEAR, // 
		EASE_IN, // Slow start, fast end
		EASE_OUT, // Fast start, slow end
		EASE_IN_OUT // Slow start and end, fast middle
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private float startParam; // Parameter value where fade starts
	private float endParam; // Parameter value where fade ends
	private float startGain; // Gain at start of fade (typically 0.0 or 1.0)
	private float endGain; // Gain at end of fade (typically 1.0 or 0.0)
	private FadeCurve curve;
	private boolean enabled;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ParamFade() {
		this.startParam = 0.0f;
		this.endParam = 1.0f;
		this.startGain = 0.0f;
		this.endGain = 1.0f;
		this.curve = FadeCurve.LINEAR;
		this.enabled = false;
	}

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * Configure a fade-in effect
	 * @param startParam Parameter value where fade starts (e.g., 0.0)
	 * @param endParam Parameter value where fade completes (e.g., 0.3)
	 * @param curve The interpolation curve to use
	 */
	public void configureFadeIn(float startParam, float endParam, FadeCurve curve) {
		this.startParam = startParam;
		this.endParam = endParam;
		this.startGain = 0.0f;
		this.endGain = 1.0f;
		this.curve = curve;
		this.enabled = true;
	}

	/**
	 * Configure a fade-out effect
	 * @param startParam Parameter value where fade starts (e.g., 0.7)
	 * @param endParam Parameter value where fade completes (e.g., 1.0)
	 * @param curve The interpolation curve to use
	 */
	public void configureFadeOut(float startParam, float endParam, FadeCurve curve) {
		this.startParam = startParam;
		this.endParam = endParam;
		this.startGain = 1.0f;
		this.endGain = 0.0f;
		this.curve = curve;
		this.enabled = true;
	}

	/**
	 * Configure a custom fade with specific gain values
	 * @param startParam Parameter value where fade starts
	 * @param endParam Parameter value where fade ends
	 * @param startGain Gain at start of fade
	 * @param endGain Gain at end of fade
	 * @param curve The interpolation curve to use
	 */
	public void configure(float startParam, float endParam, float startGain, float endGain, FadeCurve curve) {
		this.startParam = startParam;
		this.endParam = endParam;
		this.startGain = startGain;
		this.endGain = endGain;
		this.curve = curve;
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
	}

	/**
	 * Calculate the fade gain multiplier for the given parameter
	 * @param param Current parameter value [0,1]
	 * @return Gain multiplier [0,1]
	 */
	public float calculateGain(float param) {
		if (!enabled) {
			return 1.0f;
		}

		// Clamp parameter to fade range
		if (param <= startParam) {
			return startGain;
		}
		if (param >= endParam) {
			return endGain;
		}

		// Calculate normalized position within fade range
		float t = (param - startParam) / (endParam - startParam);

		// Apply curve
		float curvedT = applyCurve(t, curve);

		// Interpolate between start and end gain
		return InterpolationHelper.lerp(startGain, endGain, curvedT);
	}

	private float applyCurve(float t, FadeCurve curve) {
		switch (curve) {
		case LINEAR:
			return t;
		case EASE_IN:
			return t * t; // Quadratic ease in
		case EASE_OUT:
			return 1.0f - (1.0f - t) * (1.0f - t); // Quadratic ease out
		case EASE_IN_OUT:
			if (t < 0.5f) {
				return 2.0f * t * t; // Ease in for first half
			} else {
				float temp = 2.0f * t - 1.0f;
				return 1.0f - 0.5f * (1.0f - temp) * (1.0f - temp); // Ease out for second half
			}
		default:
			return t;
		}
	}
}
