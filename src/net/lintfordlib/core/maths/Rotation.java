package net.lintfordlib.core.maths;

public class Rotation {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float sin;
	private float cos;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float sin() {
		return this.sin;
	}

	public float cos() {
		return this.cos;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void set(float angleInRads) {
		sin = (float) Math.sin(angleInRads);
		cos = (float) Math.cos(angleInRads);
	}

	public void setIdentity() {
		sin = 0.f;
		cos = 1.f;
	}

	public float getAngle() {
		return (float) Math.atan2(sin, cos);
	}

	public Vector2f getXAxis(Vector2f inVector2f) {
		return inVector2f.set(cos, sin);
	}

	public Vector2f getYAxis(Vector2f inVector2f) {
		return inVector2f.set(-sin, cos);
	}

}
