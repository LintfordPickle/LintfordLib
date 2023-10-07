package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.physics.collisions.ContactManifold;

public interface ICollisionResolver {

	public abstract void resolveCollisions(ContactManifold manifold);

}
