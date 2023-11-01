package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.shapes.BaseShape;

public class ContactManifold {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean isIntersecting;
	public boolean enableResolveContact;

	public float impulseX;
	public float impulseY;

	// TOOD: Rename to shape
	public BaseShape shapeA;
	public BaseShape shapeB;

	public float depth;
	public final Vector2f normal = new Vector2f();

	public final Vector2f contact1 = new Vector2f();
	public final Vector2f contact2 = new Vector2f();
	public int contactCount;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInUse() {
		return isIntersecting;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialize(BaseShape bodyA, BaseShape bodyB) {
		this.shapeA = bodyA;
		this.shapeB = bodyB;

		enableResolveContact = true;

		isIntersecting = false;
		normal.set(0, 0);

		contactCount = 0;
	}
}