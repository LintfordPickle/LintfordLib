package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.Fixture;

public class ContactManifold {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean isIntersecting;
	public boolean enableResolveContact;

	public float impulseX;
	public float impulseY;

	public Fixture fixtureA;
	public Fixture fixtureB;

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

	public void initialize(Fixture fixtureA, Fixture fixtureB, float normalX, float normalY, float depth, float contact1X, float contact1Y, float contact2X, float contact2Y, int contactCount) {

		this.fixtureA = fixtureA;
		this.fixtureB = fixtureB;
		this.normal.x = normalX;
		this.normal.y = normalY;
		this.depth = depth;

		this.contact1.x = contact1X;
		this.contact1.y = contact1Y;

		this.contact2.x = contact2X;
		this.contact2.y = contact2Y;

		this.contactCount = contactCount;
		isIntersecting = contactCount > 0;
	}

	public void reset() {
		fixtureA = null;
		fixtureB = null;
		enableResolveContact = true;

		isIntersecting = false;
		normal.set(0, 0);

		contactCount = 0;
	}
}