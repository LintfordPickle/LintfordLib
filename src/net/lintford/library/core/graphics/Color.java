package net.lintford.library.core.graphics;

public class Color {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float r, g, b, a;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Color() {
		this(1.f, 1.f, 1.f);

	}

	public Color(float pRed, float pGreen, float pBlue) {
		this(pRed, pGreen, pBlue, 1.f);

	}

	public Color(float pRed, float pGreen, float pBlue, float pAlpha) {
		r = pRed;
		g = pGreen;
		b = pBlue;
		a = pAlpha;

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
