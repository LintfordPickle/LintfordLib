package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.collisions.ContactManifold;

public class CollisionResolverSimple implements ICollisionResolver {

	// Provides linear collision response along the contact normal

	@Override
	public void resolveCollisions(ContactManifold manifold) {
		final var lBodyA = manifold.bodyA;
		final var lBodyB = manifold.bodyB;

		final float relVelX = lBodyB.vx - lBodyA.vx;
		final float relVelY = lBodyB.vy - lBodyA.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, manifold.normal.x, manifold.normal.y);

		if (dotVelNor >= 0.f)
			return;

		final float minRestitution = Math.min(lBodyA.restitution(), lBodyB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (lBodyA.invMass() + lBodyB.invMass());

		final float impulseX = j * manifold.normal.x;
		final float impulseY = j * manifold.normal.y;

		lBodyA.vx -= impulseX * lBodyA.invMass();
		lBodyA.vy -= impulseY * lBodyA.invMass();

		lBodyB.vx += impulseX * lBodyB.invMass();
		lBodyB.vy += impulseY * lBodyB.invMass();

	}

}
