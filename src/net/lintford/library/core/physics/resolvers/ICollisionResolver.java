package net.lintford.library.core.physics.resolvers;

import net.lintford.library.core.physics.collisions.ContactManifold;

public interface ICollisionResolver {

	public abstract void resolveCollisions(ContactManifold manifold);

}
