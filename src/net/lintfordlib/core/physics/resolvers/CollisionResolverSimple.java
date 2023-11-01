package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.collisions.ContactManifold;

public class CollisionResolverSimple implements ICollisionResolver {

	/***
	 * Provides simple linear collision response along the contact normal.
	 */
	@Override
	public void resolveCollisions(ContactManifold contact) {
		final var lShapeA = contact.shapeA;
		final var lShapeB = contact.shapeB;

		final var lBodyA = lShapeA.parentBody();
		final var lBodyB = lShapeB.parentBody();

		final float relVelX = lBodyB.vx - lBodyA.vx;
		final float relVelY = lBodyB.vy - lBodyA.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, contact.normal.x, contact.normal.y);

		if (dotVelNor >= 0.f)
			return;

		final float minRestitution = Math.min(lShapeA.restitution(), lShapeB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (lBodyA.invMass() + lBodyB.invMass());

		final float impulseX = j * contact.normal.x;
		final float impulseY = j * contact.normal.y;

		lBodyA.vx -= impulseX * lBodyA.invMass();
		lBodyA.vy -= impulseY * lBodyA.invMass();

		lBodyB.vx += impulseX * lBodyB.invMass();
		lBodyB.vy += impulseY * lBodyB.invMass();

	}

}
