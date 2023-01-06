package net.lintford.library.core.collisions.resolvers;

import net.lintford.library.core.collisions.ContactManifold;

public interface ICollisionResolver {

	public abstract void resolveCollisions(ContactManifold manifold);

}
