package net.lintford.library;

public class ConstantsPhysics {

	// --------------------------------------
	// Statics
	// --------------------------------------

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

	public static float toPixels(float pUnits) {
		return UNITS_TO_PIXELS * pUnits;
	}

	public static float toUnits(float pPixels) {
		return PIXELS_TO_UNITS * pPixels;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setPhysicsWorldConstants(float pNumPixelsPerUnit) {
		UNITS_TO_PIXELS = pNumPixelsPerUnit;
		PIXELS_TO_UNITS = 1f / UNITS_TO_PIXELS;

	}

}
