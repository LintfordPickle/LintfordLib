package net.lintford.library.core.collisions;

public interface ICollisionCallback {

	void preContact(ContactManifold manifold);

	void postContact(ContactManifold manifold);

	void preSolve(ContactManifold manifold);

	void postSolve(ContactManifold manifold);

}
