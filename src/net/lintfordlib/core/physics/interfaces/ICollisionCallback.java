package net.lintfordlib.core.physics.interfaces;

import net.lintfordlib.core.physics.collisions.ContactManifold;

public interface ICollisionCallback {

	void preContact(ContactManifold manifold);

	void postContact(ContactManifold manifold);

	void preSolve(ContactManifold manifold);

	void postSolve(ContactManifold manifold);

}
