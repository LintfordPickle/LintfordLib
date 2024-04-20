package net.lintfordlib.core.maths;

public class Rotation {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float s = 0.f;
	public float c = 1.f;
	
	// --------------------------------------
	// Methods
	// --------------------------------------

	public void set(Rotation o) {
		s = o.s;
		c = o.c;
	}

	public void set(float angleInRads) {
		c = (float) Math.cos(angleInRads);
		s = (float) Math.sin(angleInRads);
	}

	public void setIdentity() {
		s = 0.f;
		c = 1.f;
	}

	public float getAngle() {
		return (float) Math.atan2(s, c);
	}

	public Vector2f getXAxis(Vector2f inVector2f) {
		return inVector2f.set(c, s);
	}

	public Vector2f getYAxis(Vector2f inVector2f) {
		return inVector2f.set(-s, c);
	}
}
