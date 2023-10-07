package net.lintfordlib;

import net.lintfordlib.core.maths.Vector2f;

public class ConstantsPhysics {

	// --------------------------------------
	// Statics
	// --------------------------------------

	public static final int MIN_ITERATIONS = 1;
	public static final int MAX_ITERATIONS = 128;

	public static final float EPSILON = 0.0005f; // .5 mm

	private static float UNITS_TO_PIXELS = 32.f;
	private static float PIXELS_TO_UNITS = 1.f / UNITS_TO_PIXELS;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public static float UnitsToPixels() {
		return UNITS_TO_PIXELS;
	}

	public static float PixelsToUnits() {
		return PIXELS_TO_UNITS;
	}

	public static float toPixels(float units) {
		return UNITS_TO_PIXELS * units;
	}

	public static float toUnits(float pixels) {
		return PIXELS_TO_UNITS * pixels;
	}

	public static void vector2fToUnits(Vector2f input) {
		input.x *= PIXELS_TO_UNITS;
		input.y *= PIXELS_TO_UNITS;
	}
	
	public static void vector2fToPixels(Vector2f input) {
		input.x *= UNITS_TO_PIXELS;
		input.y *= UNITS_TO_PIXELS;
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------

	public static void setPhysicsWorldConstants(float numPixelsPerUnit) {
		UNITS_TO_PIXELS = numPixelsPerUnit;
		PIXELS_TO_UNITS = 1f / UNITS_TO_PIXELS;
	}
}
