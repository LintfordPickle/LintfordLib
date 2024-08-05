package net.lintfordlib.core.maths;

public class Transform {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final Transform Identity = new Transform();

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Vector2f p = new Vector2f();
	public final Rotation q = new Rotation();
	public float angle;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setAngle(float a) {
		angle = MathHelper.wrapAngle(a);

		q.set(angle);
	}

	public void applyAngle(float a) {
		angle = MathHelper.wrapAngle(angle + a);

		q.set(angle);
	}

	public void set(Transform o) {
		p.set(o.p);
		q.set(o.q);
		angle = o.angle;
	}

	public boolean compare(Transform o) {
		return p.x == o.p.x && p.y == o.p.y && angle == o.angle && q.getAngle() == o.q.getAngle();
	}

	public void setPosition(float x, float y) {
		p.x = x;
		p.y = y;
	}

}
