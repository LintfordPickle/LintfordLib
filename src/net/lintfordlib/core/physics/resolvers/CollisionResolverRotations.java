package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.collisions.ContactManifold;

public class CollisionResolverRotations implements ICollisionResolver {

	@Override
	public void resolveCollisions(ContactManifold contact) {
		final var lBodyA = contact.bodyA;
		final var lBodyB = contact.bodyB;
		final float normalX = contact.normal.x;
		final float normalY = contact.normal.y;

		final float e = Math.min(lBodyA.restitution(), lBodyB.restitution());

		float impulse1X = 0, impulse1Y = 0, impulse2X = 0, impulse2Y = 0;
		float ra1X = 0, ra1Y = 0, ra2X = 0, ra2Y = 0;
		float rb1X = 0, rb1Y = 0, rb2X = 0, rb2Y = 0;

		// calculate impulses
		final int lContactCount = contact.contactCount;
		for (int i = 0; i < lContactCount; i++) {
			final float contactX = i == 0 ? contact.contact1.x : contact.contact2.x;
			final float contactY = i == 0 ? contact.contact1.y : contact.contact2.y;

			final float ra_x = contactX - lBodyA.x;
			final float ra_y = contactY - lBodyA.y;

			final float raPerp_x = -ra_y;
			final float raPerp_y = ra_x;

			final float angLinA_X = raPerp_x * lBodyA.angularVelocity;
			final float angLinA_Y = raPerp_y * lBodyA.angularVelocity;

			final float rb_x = contactX - lBodyB.x;
			final float rb_y = contactY - lBodyB.y;

			final float rbPerp_x = -rb_y;
			final float rbPerp_y = rb_x;

			final float angLinB_X = rbPerp_x * lBodyB.angularVelocity;
			final float angLinB_Y = rbPerp_y * lBodyB.angularVelocity;

			// relative velocity at POC taking into account angular velocity
			final float relVelX = (lBodyB.vx + angLinB_X) - (lBodyA.vx + angLinA_X);
			final float relVelY = (lBodyB.vy + angLinB_Y) - (lBodyA.vy + angLinA_Y);

			final float contactVelocityMagnitude = Vector2f.dot(relVelX, relVelY, normalX, normalY);

			if (contactVelocityMagnitude > 0.f)
				return;

			final float ra_perp_dot_n = Vector2f.dot(raPerp_x, raPerp_y, normalX, normalY);
			final float rb_perp_dot_n = Vector2f.dot(rbPerp_x, rbPerp_y, normalX, normalY);

			final float denominator = lBodyA.invMass() + lBodyB.invMass() + 
					(ra_perp_dot_n * ra_perp_dot_n) * lBodyA.invInertia() + 
					(rb_perp_dot_n * rb_perp_dot_n) * lBodyB.invInertia();
			
			float j = -(1.f + e) * contactVelocityMagnitude;
			j /= denominator;
			j /= lContactCount;

			if (i == 0) {
				impulse1X = j * normalX;
				impulse1Y = j * normalY;

				ra1X = ra_x;
				ra1Y = ra_y;

				rb1X = rb_x;
				rb1Y = rb_y;
			} else {
				impulse2X = j * normalX;
				impulse2Y = j * normalY;

				ra2X = ra_x;
				ra2Y = ra_y;

				rb2X = rb_x;
				rb2Y = rb_y;
			}
		}

		// apply impulses
		for (int i = 0; i < lContactCount; i++) {
			final float _impulseX = i == 0 ? impulse1X : impulse2X;
			final float _impulseY = i == 0 ? impulse1Y : impulse2Y;

			final float raX = i == 0 ? ra1X : ra2X;
			final float raY = i == 0 ? ra1Y : ra2Y;
			final float rbX = i == 0 ? rb1X : rb2X;
			final float rbY = i == 0 ? rb1Y : rb2Y;

			lBodyA.vx += -_impulseX * lBodyA.invMass();
			lBodyA.vy += -_impulseY * lBodyA.invMass();
			lBodyA.angularVelocity += -Vector2f.cross(raX, raY, _impulseX, _impulseY) * lBodyA.invInertia();

			lBodyB.vx += _impulseX * lBodyB.invMass();
			lBodyB.vy += _impulseY * lBodyB.invMass();
			lBodyB.angularVelocity += Vector2f.cross(rbX, rbY, _impulseX, _impulseY) * lBodyB.invInertia();
		}

	}

}
