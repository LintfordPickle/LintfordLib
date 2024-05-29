package net.lintfordlib.core.physics.resolvers;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.collisions.ContactManifold;
import net.lintfordlib.core.physics.dynamics.RigidBody.BodyType;

public class CollisionResolverRotationAndFriction implements ICollisionResolver {

	@Override
	public final void resolveCollisions(ContactManifold contact) {
		final var lBodyA = contact.bodyA;
		final var lBodyB = contact.bodyB;
		final float normalX = contact.normal.x;
		final float normalY = contact.normal.y;

		final var lShapeA = lBodyA.shape();
		final var lShapeB = lBodyB.shape();

		final float e = Math.min(lShapeA.restitution(), lShapeB.restitution());
		final float sf = (lShapeA.staticFriction() + lShapeB.staticFriction()) * .5f;
		final float df = (lShapeA.dynamicFriction() + lShapeB.dynamicFriction()) * .5f;

		float impulse1X = 0, impulse1Y = 0, impulse2X = 0, impulse2Y = 0;
		float fImpulse1X = 0, fImpulse1Y = 0, fImpulse2X = 0, fImpulse2Y = 0;

		float ra1X = 0, ra1Y = 0, ra2X = 0, ra2Y = 0;
		float rb1X = 0, rb1Y = 0, rb2X = 0, rb2Y = 0;
		float j1 = 0, j2 = 0;

		// calculate impulses / angular velocity
		final int lContactCount = contact.contactCount;
		for (int i = 0; i < lContactCount; i++) {
			final float contactX = i == 0 ? contact.contact1.x : contact.contact2.x;
			final float contactY = i == 0 ? contact.contact1.y : contact.contact2.y;

			final float ra_x = contactX - lBodyA.transform.p.x;
			final float ra_y = contactY - lBodyA.transform.p.y;

			final float raPerp_x = -ra_y;
			final float raPerp_y = ra_x;

			final float angLinA_X = raPerp_x * lBodyA.angularVelocity();
			final float angLinA_Y = raPerp_y * lBodyA.angularVelocity();

			final float rb_x = contactX - lBodyB.transform.p.x;
			final float rb_y = contactY - lBodyB.transform.p.y;

			final float rbPerp_x = -rb_y;
			final float rbPerp_y = rb_x;

			final float angLinB_X = rbPerp_x * lBodyB.angularVelocity();
			final float angLinB_Y = rbPerp_y * lBodyB.angularVelocity();

			// relative velocity at POC taking into account angular velocity
			final float relVelX = (lBodyB.vx + angLinB_X) - (lBodyA.vx + angLinA_X);
			final float relVelY = (lBodyB.vy + angLinB_Y) - (lBodyA.vy + angLinA_Y);

			final float contactVelocityMagnitude = Vector2f.dot(relVelX, relVelY, normalX, normalY);

			if (contactVelocityMagnitude > 0.f)
				return;

			final float ra_perp_dot_n = Vector2f.dot(raPerp_x, raPerp_y, normalX, normalY);
			final float rb_perp_dot_n = Vector2f.dot(rbPerp_x, rbPerp_y, normalX, normalY);

			float j = -(1.f + e) * contactVelocityMagnitude;

			final var termA = (ra_perp_dot_n * ra_perp_dot_n) * lBodyA.invInertia();
			final var termB = (rb_perp_dot_n * rb_perp_dot_n) * lBodyB.invInertia();

			j /= (lBodyA.invMass() + lBodyB.invMass()) + termA + termB;
			j /= lContactCount;

			if (i == 0) {
				impulse1X = j * normalX;
				impulse1Y = j * normalY;

				ra1X = ra_x;
				ra1Y = ra_y;

				rb1X = rb_x;
				rb1Y = rb_y;

				j1 = j;
			} else {
				impulse2X = j * normalX;
				impulse2Y = j * normalY;

				ra2X = ra_x;
				ra2Y = ra_y;

				rb2X = rb_x;
				rb2Y = rb_y;

				j2 = j;
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

			if (lBodyA.bodyType() == BodyType.Dynamic) {
				lBodyA.vx += -_impulseX * lBodyA.invMass();
				lBodyA.vy += -_impulseY * lBodyA.invMass();
				lBodyA.applyAngularVelocity(-Vector2f.cross(raX, raY, _impulseX, _impulseY) * lBodyA.invInertia());

			}

			if (lBodyB.bodyType() == BodyType.Dynamic) {
				lBodyB.vx += _impulseX * lBodyB.invMass();
				lBodyB.vy += _impulseY * lBodyB.invMass();
				lBodyB.applyAngularVelocity(Vector2f.cross(rbX, rbY, _impulseX, _impulseY) * lBodyB.invInertia());

			}

			contact.impulseX = _impulseX;
			contact.impulseY = _impulseY;
		}

		// -----

		// calculate impulses / friction
		for (int i = 0; i < lContactCount; i++) {
			final float contactX = i == 0 ? contact.contact1.x : contact.contact2.x;
			final float contactY = i == 0 ? contact.contact1.y : contact.contact2.y;

			final float ra_x = contactX - lBodyA.transform.p.x;
			final float ra_y = contactY - lBodyA.transform.p.y;

			final float raPerp_x = -ra_y;
			final float raPerp_y = ra_x;

			final float angLinA_X = raPerp_x * lBodyA.angularVelocity();
			final float angLinA_Y = raPerp_y * lBodyA.angularVelocity();

			final float rb_x = contactX - lBodyB.transform.p.x;
			final float rb_y = contactY - lBodyB.transform.p.y;

			final float rbPerp_x = -rb_y;
			final float rbPerp_y = rb_x;

			final float angLinB_X = rbPerp_x * lBodyB.angularVelocity();
			final float angLinB_Y = rbPerp_y * lBodyB.angularVelocity();

			// relative velocity at POC taking into account angular velocity
			final float relVelX = (lBodyB.vx + angLinB_X) - (lBodyA.vx + angLinA_X);
			final float relVelY = (lBodyB.vy + angLinB_Y) - (lBodyA.vy + angLinA_Y);

			final float d = Vector2f.dot(relVelX, relVelY, normalX, normalY);
			float tangent_X = relVelX - d * normalX;
			float tangent_Y = relVelY - d * normalY;

			if (MathHelper.equalWithinEpsilon(tangent_X, 0) && MathHelper.equalWithinEpsilon(tangent_Y, 0))
				continue;

			final float tangentLength = (float) Math.sqrt(tangent_X * tangent_X + tangent_Y * tangent_Y);
			tangent_X /= tangentLength;
			tangent_Y /= tangentLength;

			final float ra_perp_dot_t = Vector2f.dot(raPerp_x, raPerp_y, tangent_X, tangent_Y);
			final float rb_perp_dot_t = Vector2f.dot(rbPerp_x, rbPerp_y, tangent_X, tangent_Y);

			float jt = -Vector2f.dot(relVelX, relVelY, tangent_X, tangent_Y);

			final var termA = (ra_perp_dot_t * ra_perp_dot_t) * lBodyA.invInertia();
			final var termB = (rb_perp_dot_t * rb_perp_dot_t) * lBodyB.invInertia();

			jt /= (lBodyA.invMass() + lBodyB.invMass()) + termA + termB;
			jt /= lContactCount;

			if (i == 0) {
				final float j = j1;

				if (Math.abs(jt) <= j * sf) {
					fImpulse1X = jt * tangent_X;
					fImpulse1Y = jt * tangent_Y;
				} else {
					fImpulse1X = -j * tangent_X * df;
					fImpulse1Y = -j * tangent_Y * df;
				}

				ra1X = ra_x;
				ra1Y = ra_y;

				rb1X = rb_x;
				rb1Y = rb_y;
			} else {
				final float j = j2;

				if (Math.abs(jt) <= j * sf) {
					fImpulse2X = jt * tangent_X;
					fImpulse2Y = jt * tangent_Y;
				} else {
					fImpulse2X = -j * tangent_X * df;
					fImpulse2Y = -j * tangent_Y * df;
				}

				ra2X = ra_x;
				ra2Y = ra_y;

				rb2X = rb_x;
				rb2Y = rb_y;
			}
		}

		// apply impulses
		for (int i = 0; i < lContactCount; i++) {
			final float _impulseX = i == 0 ? fImpulse1X : fImpulse2X;
			final float _impulseY = i == 0 ? fImpulse1Y : fImpulse2Y;

			final float raX = i == 0 ? ra1X : ra2X;
			final float raY = i == 0 ? ra1Y : ra2Y;
			final float rbX = i == 0 ? rb1X : rb2X;
			final float rbY = i == 0 ? rb1Y : rb2Y;

			if (lBodyA.bodyType() == BodyType.Dynamic) {
				lBodyA.vx += -_impulseX * lBodyA.invMass();
				lBodyA.vy += -_impulseY * lBodyA.invMass();
				lBodyA.applyAngularVelocity(-Vector2f.cross(raX, raY, _impulseX, _impulseY) * lBodyA.invInertia());

			}

			if (lBodyB.bodyType() == BodyType.Dynamic) {
				lBodyB.vx += _impulseX * lBodyB.invMass();
				lBodyB.vy += _impulseY * lBodyB.invMass();
				lBodyB.applyAngularVelocity(Vector2f.cross(rbX, rbY, _impulseX, _impulseY) * lBodyB.invInertia());
			}
		}
	}
}
