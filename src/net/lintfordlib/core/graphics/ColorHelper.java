package net.lintfordlib.core.graphics;

public class ColorHelper {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/*
	 You can a 
	 */
	
	public static final float COLORWHEEL_OFFSET_RED = 0.f;
	public static final float COLORWHEEL_OFFSET_YELLOW = 30.f;
	public static final float COLORWHEEL_OFFSET_GREEN= 60.f;
	public static final float COLORWHEEL_OFFSET_BLUE = 120.f;
	public static final float COLORWHEEL_OFFSET_PINK = 240.f;
	
	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Converts RGB (all in [0..1]) to HSV. Results set in 'out' float array: [hue (0-360), saturation (0-1), value (0-1)].
	 */
	public static void rgbToHsv(float r, float g, float b, float[] out) {
		assert (out != null) : "rgbToHsv expects a valid out array to be passed";
		assert (out.length >= 3) : "rgbToHsv expects the out array to have at least 3 elements (H,S,V)";

		final var max = Math.max(r, Math.max(g, b));
		final var min = Math.min(r, Math.min(g, b));
		final var delta = max - min;

		float h = 0f;

		if (delta == 0) {
			h = 0; // Undefined hue, grayscale color
		} else {
			if (max == r) {
				h = 60 * (((g - b) / delta) % 6);
			} else if (max == g) {
				h = 60 * (((b - r) / delta) + 2);
			} else { // max == b
				h = 60 * (((r - g) / delta) + 4);
			}
			if (h < 0)
				h += 360;
		}

		out[0] = h;
		out[1] = (max == 0) ? 0 : (delta / max);
		out[2] = max;
	}

	/**
	 * Converts hsv to rgb values. Results set in 'out' float array: [red (0-1), green (0-1), blue (0-1)] .
	 */
	public static void hsvToRgb(float h, float s, float v, float[] out) {
		assert (out != null) : "hsvToRgb expects a valid out array to be passed";
		assert (out.length >= 3) : "hsvToRgb expects the out array to have at least 3 elements (H,S,V)";

		final var c = v * s;
		final var x = c * (1 - Math.abs((h / 60.0f % 2) - 1));
		final var m = v - c;

		float r = 0, g = 0, b = 0;

		if (h < 60) {
			r = c;
			g = x;
			b = 0;
		} else if (h < 120) {
			r = x;
			g = c;
			b = 0;
		} else if (h < 180) {
			r = 0;
			g = c;
			b = x;
		} else if (h < 240) {
			r = 0;
			g = x;
			b = c;
		} else if (h < 300) {
			r = x;
			g = 0;
			b = c;
		} else {
			r = c;
			g = 0;
			b = x;
		}

		out[0] = r + m;
		out[1] = g + m;
		out[2] = b + m;
	}
}
