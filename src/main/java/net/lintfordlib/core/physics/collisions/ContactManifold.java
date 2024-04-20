package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;

public class ContactManifold {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean isIntersecting;
	public boolean enableResolveContact;

	public float impulseX;
	public float impulseY;

	public RigidBody bodyA;
	public RigidBody bodyB;

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

	public void initialize(RigidBody bodyA, RigidBody bodyB) {
		this.bodyA = bodyA;
		this.bodyB = bodyB;

		enableResolveContact = true;

		isIntersecting = false;
		normal.set(0, 0);

		contactCount = 0;
	}
}