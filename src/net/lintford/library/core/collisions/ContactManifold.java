package net.lintford.library.core.collisions;

import net.lintford.library.core.maths.Vector2f;

public class ContactManifold {

	// --------------------------------------
	// Variables
	// --------------------------------------
	
	public boolean intersection;

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
		return intersection;
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------
	
	public void initialize(RigidBody bodyA, RigidBody bodyB, 
			float normalX, float normalY, float depth, 
			float contact1X, float contact1Y, 
			float contact2X, float contact2Y, int contactCount) {

		this.bodyA = bodyA;
		this.bodyB = bodyB;
		this.normal.x = normalX;
		this.normal.y = normalY;
		this.depth = depth;

		this.contact1.x = contact1X;
		this.contact1.y = contact1Y;

		this.contact2.x = contact2X;
		this.contact2.y = contact2Y;

		this.contactCount = contactCount;
		intersection = contactCount > 0;
	}

	public void reset() {
		bodyA = null;
		bodyB = null;

		intersection = false;
		normal.set(0, 0);

		contactCount = 0;
	}

}
