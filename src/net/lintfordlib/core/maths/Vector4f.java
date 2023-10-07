package net.lintfordlib.core.maths;

import java.io.Serializable;

public class Vector4f implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6738480994651702671L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x, y, z, w;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Vector4f() {
		x = y = z = w = 0;
	}

	public Vector4f(float pValue) {
		x = y = z = w = pValue;
	}

	public Vector4f(float pX, float pY, float pZ, float pW) {
		x = pX;
		y = pY;
		z = pZ;
		w = pW;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public void xyzw(float pX, float pY, float pZ, float pW) {
		x = pX;
		y = pY;
		z = pZ;
		w = pW;
	}

}
