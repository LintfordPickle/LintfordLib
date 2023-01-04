package net.lintford.library.core.collisions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStatTagCaption;
import net.lintford.library.core.debug.stats.DebugStatTagFloat;
import net.lintford.library.core.debug.stats.DebugStatTagInt;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.time.TimeConstants;

public class PhysicsWorld {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	private class CollisionPair {
		public int bodyAUid;
		public int bodyBUid;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final ContactManifold mContactManifold = new ContactManifold();

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mGravityX;
	private float mGravityY; // mps/s

	private final List<RigidBody> mBodies = new ArrayList<>();

	private final LinkedList<CollisionPair> mCollisionPairPool = new LinkedList<>();
	private final List<CollisionPair> mCollisionPair = new ArrayList<>(16);

	private DebugStatTagCaption mDebugStatPhysicsCaption;
	private DebugStatTagInt mDebugStatsNumBodies;
	private DebugStatTagFloat mDebugStepTimeInMm;
	private DebugStatTagInt mDebugNumIterations;

	private boolean mInitialized = false;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<RigidBody> bodies() {
		return mBodies;
	}

	public int numBodies() {
		return mBodies.size();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsWorld() {
		this(0.f, 0.f);
	}

	public PhysicsWorld(float gravityX, float gravityY) {
		mGravityX = gravityX;
		mGravityY = gravityY;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		initializeCollisionPairPool();

		// TODO: Only do this if in debug mode
		mDebugStatPhysicsCaption = new DebugStatTagCaption("Physics");
		mDebugStatsNumBodies = new DebugStatTagInt("Num Bodies", 0, false);
		mDebugStepTimeInMm = new DebugStatTagFloat("step", 0.0f, false);
		mDebugNumIterations = new DebugStatTagInt("Num Iterations", 0, false);

		Debug.debugManager().stats().addCustomStatTag(mDebugStatPhysicsCaption);
		Debug.debugManager().stats().addCustomStatTag(mDebugStatsNumBodies);
		Debug.debugManager().stats().addCustomStatTag(mDebugStepTimeInMm);
		Debug.debugManager().stats().addCustomStatTag(mDebugNumIterations);

		mInitialized = true;
	}

	private void initializeCollisionPairPool() {
		for (int i = 0; i < 32; i++) {
			mCollisionPairPool.addLast(new CollisionPair());
		}
	}

	public void unload() {
		Debug.debugManager().stats().removeCustomStatTag(mDebugStatPhysicsCaption);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStatsNumBodies);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStepTimeInMm);
		Debug.debugManager().stats().removeCustomStatTag(mDebugNumIterations);

		mDebugStatPhysicsCaption = null;
		mDebugStatsNumBodies = null;
		mDebugStepTimeInMm = null;
		mDebugNumIterations = null;

		mInitialized = false;
	}

	public void stepWorld(float time, int totalIterations) {
		if (mInitialized == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot step physics world - not initialized");
			return;
		}

		totalIterations = MathHelper.clampi(totalIterations, ConstantsPhysics.MIN_ITERATIONS, ConstantsPhysics.MAX_ITERATIONS);
		mDebugNumIterations.setValue(totalIterations);
		mDebugStatsNumBodies.setValue(numBodies());

		final var lSystemTimeBegin = System.nanoTime();

		time /= (float) totalIterations;
		for (int it = 0; it < totalIterations; it++) {

			mCollisionPair.clear();
			stepBodies(time);

			broadPhase();

			narrowPhase();
		}

		final var lDelta = ((System.nanoTime() - lSystemTimeBegin) / TimeConstants.NanoToMilli);
		mDebugStepTimeInMm.setValue((float) lDelta);
	}

	private void stepBodies(float time) {
		final int lNumBodies = mBodies.size();
		for (int i = 0; i < lNumBodies; i++) {
			mBodies.get(i).step(time, mGravityX, mGravityY);
		}
	}

	private void broadPhase() {
		final var lNumBodies = mBodies.size();
		for (int i = 0; i < lNumBodies - 1; i++) {
			final var lBodyA = mBodies.get(i);
			final var lBodyA_aabb = lBodyA.aabb();

			for (int j = i + 1; j < lNumBodies; j++) {
				final var lBodyB = mBodies.get(j);
				final var lBodyB_aabb = lBodyB.aabb();

				if (lBodyA.isStatic() && lBodyB.isStatic())
					continue;

				if (lBodyA_aabb.intersectsAA(lBodyB_aabb) == false)
					continue;

				final var lCollisionPair = getFreeCollisionPair();
				lCollisionPair.bodyAUid = i;
				lCollisionPair.bodyBUid = j;

				mCollisionPair.add(lCollisionPair);
			}
		}
	}

	private void narrowPhase() {
		final var lNumCollisionPairs = mCollisionPair.size();
		for (int i = 0; i < lNumCollisionPairs; i++) {
			final var lCollisionPair = mCollisionPair.get(i);

			final var lBodyA = mBodies.get(lCollisionPair.bodyAUid);
			final var lBodyB = mBodies.get(lCollisionPair.bodyBUid);

			if (SAT.checkCollides(lBodyA, lBodyB, mContactManifold)) {
				separateBodiesByMTV(lBodyA, lBodyB, mContactManifold.normal.x * mContactManifold.depth, mContactManifold.normal.y * mContactManifold.depth);

				SAT.fillContactPoints(mContactManifold);

				resolveCollisionRotationFriction(mContactManifold);
			}

			returnCollisionPair(lCollisionPair);
		}
	}

	@SuppressWarnings("unused")
	private void resolveCollisionBasic(final ContactManifold contact) {
		final var lBodyA = contact.bodyA;
		final var lBodyB = contact.bodyB;

		final float relVelX = lBodyB.vx - lBodyA.vx;
		final float relVelY = lBodyB.vy - lBodyA.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, mContactManifold.normal.x, mContactManifold.normal.y);

		if (dotVelNor >= 0.f)
			return;

		final float minRestitution = Math.min(lBodyA.restitution(), lBodyB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (lBodyA.invMass() + lBodyB.invMass());

		final float impulseX = j * mContactManifold.normal.x;
		final float impulseY = j * mContactManifold.normal.y;

		lBodyA.vx -= impulseX * lBodyA.invMass();
		lBodyA.vy -= impulseY * lBodyA.invMass();

		lBodyB.vx += impulseX * lBodyB.invMass();
		lBodyB.vy += impulseY * lBodyB.invMass();
	}

	@SuppressWarnings("unused")
	private void resolveCollisionRotation(final ContactManifold contact) {
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
			final float relVelX = lBodyB.vx + angLinB_X - lBodyA.vx + angLinA_X;
			final float relVelY = lBodyB.vy + angLinB_Y - lBodyA.vy + angLinA_Y;

			final float contactVelocityMagnitude = Vector2f.dot(relVelX, relVelY, normalX, normalY);

			if (contactVelocityMagnitude > 0.f)
				return;

			final float ra_perp_dot_n = Vector2f.dot(raPerp_x, raPerp_y, normalX, normalY);
			final float rb_perp_dot_n = Vector2f.dot(rbPerp_x, rbPerp_y, normalX, normalY);

			float j = -(1.f + e) * contactVelocityMagnitude;
			j /= (lBodyA.invMass() + lBodyB.invMass()) + (ra_perp_dot_n * ra_perp_dot_n) * lBodyA.invInertia() + (rb_perp_dot_n * rb_perp_dot_n) * lBodyB.invInertia();
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

	private void resolveCollisionRotationFriction(final ContactManifold contact) {
		final var lBodyA = contact.bodyA;
		final var lBodyB = contact.bodyB;
		final float normalX = contact.normal.x;
		final float normalY = contact.normal.y;

		final float e = Math.min(lBodyA.restitution(), lBodyB.restitution());
		final float sf = (lBodyA.staticFriction() + lBodyB.staticFriction()) * .5f;
		final float df = (lBodyA.dynamicFriction() + lBodyB.dynamicFriction()) * .5f;

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
			final float relVelX = lBodyB.vx + angLinB_X - lBodyA.vx + angLinA_X;
			final float relVelY = lBodyB.vy + angLinB_Y - lBodyA.vy + angLinA_Y;

			final float contactVelocityMagnitude = Vector2f.dot(relVelX, relVelY, normalX, normalY);

			if (contactVelocityMagnitude > 0.f)
				return;

			final float ra_perp_dot_n = Vector2f.dot(raPerp_x, raPerp_y, normalX, normalY);
			final float rb_perp_dot_n = Vector2f.dot(rbPerp_x, rbPerp_y, normalX, normalY);

			float j = -(1.f + e) * contactVelocityMagnitude;
			j /= (lBodyA.invMass() + lBodyB.invMass()) + (ra_perp_dot_n * ra_perp_dot_n) * lBodyA.invInertia() + (rb_perp_dot_n * rb_perp_dot_n) * lBodyB.invInertia();
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

			lBodyA.vx += -_impulseX * lBodyA.invMass();
			lBodyA.vy += -_impulseY * lBodyA.invMass();
			lBodyA.angularVelocity += -Vector2f.cross(raX, raY, _impulseX, _impulseY) * lBodyA.invInertia();

			lBodyB.vx += _impulseX * lBodyB.invMass();
			lBodyB.vy += _impulseY * lBodyB.invMass();
			lBodyB.angularVelocity += Vector2f.cross(rbX, rbY, _impulseX, _impulseY) * lBodyB.invInertia();
		}

		// -----

		// calculate impulses / friction
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
			final float relVelX = lBodyB.vx + angLinB_X - lBodyA.vx + angLinA_X;
			final float relVelY = lBodyB.vy + angLinB_Y - lBodyA.vy + angLinA_Y;

			final float d = Vector2f.dot(relVelX, relVelY, normalX, normalY);
			float tangent_X = relVelX - d * normalX;
			float tangent_Y = relVelY - d * normalY;

			if (SAT.equalWithinEpsilon(tangent_X, 0) && SAT.equalWithinEpsilon(tangent_Y, 0))
				continue;

			final float tangentLength = (float) Math.sqrt(tangent_X * tangent_X + tangent_Y * tangent_Y);
			tangent_X /= tangentLength;
			tangent_Y /= tangentLength;

			final float ra_perp_dot_t = Vector2f.dot(raPerp_x, raPerp_y, tangent_X, tangent_Y);
			final float rb_perp_dot_t = Vector2f.dot(rbPerp_x, rbPerp_y, tangent_X, tangent_Y);

			float jt = -Vector2f.dot(relVelX, relVelY, tangent_X, tangent_Y);
			jt /= (lBodyA.invMass() + lBodyB.invMass()) + (ra_perp_dot_t * ra_perp_dot_t) * lBodyA.invInertia() + (rb_perp_dot_t * rb_perp_dot_t) * lBodyB.invInertia();
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

			lBodyA.vx += -_impulseX * lBodyA.invMass();
			lBodyA.vy += -_impulseY * lBodyA.invMass();
			lBodyA.angularVelocity += -Vector2f.cross(raX, raY, _impulseX, _impulseY) * lBodyA.invInertia();

			lBodyB.vx += _impulseX * lBodyB.invMass();
			lBodyB.vy += _impulseY * lBodyB.invMass();
			lBodyB.angularVelocity += Vector2f.cross(rbX, rbY, _impulseX, _impulseY) * lBodyB.invInertia();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void separateBodiesByMTV(final RigidBody lBodyA, final RigidBody lBodyB, float mtvX, float mtvY) {
		if (lBodyA.isStatic()) {
			lBodyB.x += mtvX;
			lBodyB.y += mtvY;
		} else if (lBodyB.isStatic()) {
			lBodyA.x -= mtvX;
			lBodyA.y -= mtvY;
		} else {
			lBodyA.x += -mtvX / 2.f;
			lBodyA.y += -mtvY / 2.f;

			lBodyB.x += mtvX / 2.f;
			lBodyB.y += mtvY / 2.f;
		}
	}

	public void addBody(RigidBody newBody) {
		mBodies.add(newBody);
	}

	public boolean removeBody(RigidBody body) {
		return mBodies.remove(body);
	}

	public RigidBody getBodyByIndex(int bodyIndex) {
		if (bodyIndex < 0 || bodyIndex >= mBodies.size())
			return null;

		return mBodies.get(bodyIndex);
	}

	private CollisionPair getFreeCollisionPair() {
		if (mCollisionPairPool.size() > 0) {
			final var obj = mCollisionPairPool.removeFirst();
			return obj;
		}

		enlargeCollisionPairPool(8);
		final var obj = mCollisionPairPool.removeFirst();
		return obj;
	}

	private void enlargeCollisionPairPool(final int amount) {
		for (int i = 0; i < amount; i++) {
			mCollisionPairPool.addLast(new CollisionPair());
		}
	}

	private void returnCollisionPair(CollisionPair obj) {
		obj.bodyAUid = 0;
		obj.bodyBUid = 0;
		mCollisionPairPool.addLast(obj);
	}

}
