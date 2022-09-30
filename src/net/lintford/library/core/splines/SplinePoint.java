package net.lintford.library.core.splines;

public class SplinePoint {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x;
	public float y;
	public float length = 1f;
	public float accLength = 1f; // length to this point (from start of spline)

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SplinePoint() {

	}

	public SplinePoint(float pX, float pY) {
		x = pX;
		y = pY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void set(float pX, float pY) {
		set(pX, pY, 1f);

	}

	public void set(float pX, float pY, float pL) {
		x = pX;
		y = pY;
		length = pL;
	}

	public void set(SplinePoint otherSplinePoint) {
		x = otherSplinePoint.x;
		y = otherSplinePoint.y;
		length = otherSplinePoint.length;
	}
}
