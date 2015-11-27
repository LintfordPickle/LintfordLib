package net.ld.library.core.maths;

public class Vector4f {

	// ---------------------------------
	// Variables
	// ---------------------------------

	public float x, y, z, w;

	// ---------------------------------
	// Constructors
	// ---------------------------------

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
