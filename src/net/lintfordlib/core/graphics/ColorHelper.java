package net.lintfordlib.core.graphics;

public class ColorHelper {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/*
	 * You can a
	 */

	public static final float COLORWHEEL_OFFSET_RED = 0.f;
	public static final float COLORWHEEL_OFFSET_YELLOW = 30.f;
	public static final float COLORWHEEL_OFFSET_GREEN = 60.f;
	public static final float COLORWHEEL_OFFSET_BLUE = 120.f;
	public static final float COLORWHEEL_OFFSET_PINK = 240.f;

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Converts an RGB color to HSV (Hue, Saturation, Value) color space.
	 * <p>
	 * This method transforms color components from the RGB color model to the HSV color model. The HSV representation is useful for color manipulation tasks such as adjusting brightness or saturation while preserving hue.
	 * </p>
	 *
	 * @param r   the red component in the range [0.0, 1.0]
	 * @param g   the green component in the range [0.0, 1.0]
	 * @param b   the blue component in the range [0.0, 1.0]
	 * @param out an array to store the resulting HSV values, must have at least 3 elements. out[0] will contain hue in degrees [0.0, 360.0), out[1] will contain saturation [0.0, 1.0], out[2] will contain value [0.0, 1.0]
	 * @throws AssertionError if out is null or has fewer than 3 elements (when assertions are enabled)
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
	 * Converts an HSV (Hue, Saturation, Value) color to RGB color space.
	 * <p>
	 * This method transforms color components from the HSV color model to the RGB color model. The conversion uses the standard HSV to RGB algorithm based on the hue's position in the color wheel.
	 * </p>
	 *
	 * @param h   the hue component in degrees [0.0, 360.0)
	 * @param s   the saturation component in the range [0.0, 1.0]
	 * @param v   the value (brightness) component in the range [0.0, 1.0]
	 * @param out an array to store the resulting RGB values, must have at least 3 elements. out[0] will contain red [0.0, 1.0], out[1] will contain green [0.0, 1.0], out[2] will contain blue [0.0, 1.0]
	 * @throws AssertionError if out is null or has fewer than 3 elements (when assertions are enabled)
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

	/**
	 * Applies a multiplicative tint to a color.
	 * <p>
	 * This method multiplies each RGB channel of the base color by the corresponding channel of the tint color, effectively darkening or coloring the base color. Each channel is multiplied independently and normalized by 255. The alpha channel is always set to fully opaque (0xFF).
	 * </p>
	 * <p>
	 * This operation is commonly used in graphics rendering to apply color filters or lighting effects to textures and sprites.
	 * </p>
	 *
	 * @param color the base color as a packed integer in RGB format (0xRRGGBB)
	 * @param tint  the tint color as a packed integer in RGB format (0xRRGGBB)
	 * @return the tinted color as a packed integer in ARGB format with full opacity (0xFFRRGGBB)
	 */
	public static int applyTint(int color, int tint) {
		int r = ((color >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
		int g = ((color >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
		int b = (color & 0xFF) * (tint & 0xFF) / 255;
		return (0xFF << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Performs linear interpolation between two RGB colors.
	 * <p>
	 * This method interpolates between two colors represented as packed integers in RGB format, where the alpha channel is always set to fully opaque (0xFF). The interpolation is performed independently on each color channel (red, green, blue).
	 * </p>
	 *
	 * @param c1 the first color as a packed integer in RGB format (0xRRGGBB)
	 * @param c2 the second color as a packed integer in RGB format (0xRRGGBB)
	 * @param t  the interpolation factor in the range [0.0, 1.0], where 0.0 returns c1, 1.0 returns c2, and values in between return a blend of both colors
	 * @return the interpolated color as a packed integer in ARGB format with full opacity (0xFFRRGGBB)
	 */
	public static int lerpColorRGB(int c1, int c2, float t) {
		int r1 = (c1 >> 16) & 0xFF;
		int g1 = (c1 >> 8) & 0xFF;
		int b1 = c1 & 0xFF;

		int r2 = (c2 >> 16) & 0xFF;
		int g2 = (c2 >> 8) & 0xFF;
		int b2 = c2 & 0xFF;

		int r = (int) (r1 + t * (r2 - r1));
		int g = (int) (g1 + t * (g2 - g1));
		int b = (int) (b1 + t * (b2 - b1));

		return (0xFF << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Performs linear interpolation between two ARGB colors.
	 * <p>
	 * This method interpolates between two colors represented as packed integers in ARGB format. The interpolation is performed independently on all four channels: alpha, red, green, and blue. This allows for smooth transitions between colors with different opacity levels.
	 * </p>
	 *
	 * @param c1 the first color as a packed integer in ARGB format (0xAARRGGBB)
	 * @param c2 the second color as a packed integer in ARGB format (0xAARRGGBB)
	 * @param t  the interpolation factor in the range [0.0, 1.0], where 0.0 returns c1, 1.0 returns c2, and values in between return a blend of both colors
	 * @return the interpolated color as a packed integer in ARGB format (0xAARRGGBB)
	 */
	public static int lerpColorARGB(int c1, int c2, float t) {
		int a1 = (c1 >> 24) & 0xFF;
		int r1 = (c1 >> 16) & 0xFF;
		int g1 = (c1 >> 8) & 0xFF;
		int b1 = c1 & 0xFF;

		int a2 = (c2 >> 24) & 0xFF;
		int r2 = (c2 >> 16) & 0xFF;
		int g2 = (c2 >> 8) & 0xFF;
		int b2 = c2 & 0xFF;

		int a = (int) (a1 + t * (a2 - a1));
		int r = (int) (r1 + t * (r2 - r1));
		int g = (int) (g1 + t * (g2 - g1));
		int b = (int) (b1 + t * (b2 - b1));

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

}
