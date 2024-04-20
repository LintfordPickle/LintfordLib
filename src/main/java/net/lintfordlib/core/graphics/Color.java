package net.lintfordlib.core.graphics;

public class Color {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float r, g, b;
	public float a;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Color() {
		this(1.f, 1.f, 1.f);

	}

	public Color(float red, float green, float blue) {
		this(red, green, blue, 1.f);

	}

	public Color(float red, float green, float blue, float alpha) {
		r = red;
		g = green;
		b = blue;
		a = alpha;
	}

	public Color(final Color otherColor) {
		r = otherColor.r;
		g = otherColor.g;
		b = otherColor.b;
		a = otherColor.a;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setFromColor(final Color otherColor) {
		if (otherColor == null) {
			r = 1.f;
			g = 1.f;
			b = 1.f;
			a = 1.f;
			return;
		}

		r = otherColor.r;
		g = otherColor.g;
		b = otherColor.b;
		a = otherColor.a;
	}

	public void setRGB(float red, float green, float blue) {
		r = red;
		g = green;
		b = blue;
	}

	public void setRGBA(float red, float green, float blue, float alpha) {
		r = red;
		g = green;
		b = blue;
		a = alpha;
	}

	public void setRGBA(float newComponentValues) {
		r = newComponentValues;
		g = newComponentValues;
		b = newComponentValues;
		a = newComponentValues;
	}

	public void setRGB(float newComponentValues) {
		r = newComponentValues;
		g = newComponentValues;
		b = newComponentValues;
	}
}
