package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.collisions.ContactManifold;

public class CollisionResolverSimple implements ICollisionResolver {

	// Provides linear collision response along the contact normal

	@Override
	public void resolveCollisions(ContactManifold manifold) {
		final var lBodyA = manifold.fixtureA;
		final var lBodyB = manifold.fixtureB;

		final float relVelX = lBodyB.parent.vx - lBodyA.parent.vx;
		final float relVelY = lBodyB.parent.vy - lBodyA.parent.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, manifold.normal.x, manifold.normal.y);

		if (dotVelNor >= 0.f)
			return;

		final float minRestitution = Math.min(lBodyA.restitution(), lBodyB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (lBodyA.parent.invMass() + lBodyB.parent.invMass());

		final float impulseX = j * manifold.normal.x;
		final float impulseY = j * manifold.normal.y;

		lBodyA.parent.vx -= impulseX * lBodyA.parent.invMass();
		lBodyA.parent.vy -= impulseY * lBodyA.parent.invMass();

		lBodyB.parent.vx += impulseX * lBodyB.parent.invMass();
		lBodyB.parent.vy += impulseY * lBodyB.parent.invMass();

	}

}
