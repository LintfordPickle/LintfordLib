package net.lintfordlib.core.maths;

import java.io.Serializable;

public class Vector3f implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2083121606310210831L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x, y, z;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Vector3f() {
		x = y = z = 0;
	}

	public Vector3f(float pV) {
		x = y = z = pV;
	}

	public Vector3f(float pX, float pY, float pZ) {
		x = pX;
		y = pY;
		z = pZ;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public Vector3f normalize() {
		Vector3f lReturn = new Vector3f(x, y, z);
		float length = length();
		lReturn.x /= length;
		lReturn.y /= length;
		lReturn.z /= length;
		return lReturn;
	}

	public float length() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public void set(float x) {
		this.x = x;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;

	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
