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
				separateBodiesByMTV(lBodyA, lBodyB, mContactManifold.mtv.x * mContactManifold.depth, mContactManifold.mtv.y * mContactManifold.depth);

				SAT.fillContactPoints(mContactManifold);

				final float relVelX = lBodyB.vx - lBodyA.vx;
				final float relVelY = lBodyB.vy - lBodyA.vy;

				final float dotVelNor = Vector2f.dot(relVelX, relVelY, mContactManifold.mtv.x, mContactManifold.mtv.y);

				if (dotVelNor < 0.f) {
					final float minRestitution = Math.min(lBodyA.restitution(), lBodyB.restitution());
					float j = -(1.f + minRestitution) * dotVelNor;

					j /= (lBodyA.invMass() + lBodyB.invMass());

					final float impulseX = j * mContactManifold.mtv.x;
					final float impulseY = j * mContactManifold.mtv.y;

					lBodyA.vx -= impulseX * lBodyA.invMass();
					lBodyA.vy -= impulseY * lBodyA.invMass();

					lBodyB.vx += impulseX * lBodyB.invMass();
					lBodyB.vy += impulseY * lBodyB.invMass();
				}
			}

			returnCollisionPair(lCollisionPair);
		}

		mCollisionPair.clear();
	}

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

	// --------------------------------------
	// Methods
	// --------------------------------------

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
