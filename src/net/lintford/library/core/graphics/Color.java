package net.lintford.library.core.graphics;

public class Color {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final Color White = new Color(1.f, 1.f, 1.f, 1.f);
	public static final Color Red   = new Color(1.f, 0.f, 0.f, 1.f);
	public static final Color Green = new Color(0.f, 1.f, 0.f, 1.f);
	public static final Color Blue  = new Color(0.f, 0.f, 1.f, 1.f);
	public static final Color Black = new Color(0.f, 0.f, 0.f, 1.f);

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float r, g, b, a;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Color() {

	}

	public Color(float pRed, float pGreen, float pBlue, float pAlpha) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setRGB(float pRed, float pGreen, float pBlue) {
		r = pRed;
		g = pGreen;
		b = pBlue;

	}

	public void setRGBA(float pRed, float pGreen, float pBlue, float pAlpha) {
		r = pRed;
		g = pGreen;
		b = pBlue;
		a = pAlpha;

	}

	public void setRGBA(float pNewValue) {
		r = pNewValue;
		g = pNewValue;
		b = pNewValue;
		a = pNewValue;

	}

	public void setRGB(float pNewValue) {
		r = pNewValue;
		g = pNewValue;
		b = pNewValue;

	}

}
