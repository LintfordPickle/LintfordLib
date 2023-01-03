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
	// Constants
	// --------------------------------------

	private float mGravityX;
	private float mGravityY; // mps/s

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<RigidBody> mBodies = new ArrayList<>();

	private final LinkedList<ContactManifold> mContactPool;
	private final List<ContactManifold> mContacts = new ArrayList<>(64);
	public final List<ContactManifold> mActiveContactsManifoldsList = new ArrayList<>(64);

	private DebugStatTagCaption mDebugStatPhysicsCaption;
	private DebugStatTagInt mDebugStatsNumBodies;
	private DebugStatTagFloat mDebugStepTimeInMm;
	private DebugStatTagInt mDebugNumIterations;
	private DebugStatTagInt mDebugNumContacts;

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

		mContactPool = new LinkedList<>();

		createContacts(128);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		// TODO: Only do this if in debug mode
		mDebugStatPhysicsCaption = new DebugStatTagCaption("Physics");
		mDebugStatsNumBodies = new DebugStatTagInt("Num Bodies", 0, false);
		mDebugStepTimeInMm = new DebugStatTagFloat("step", 0.0f, false);
		mDebugNumIterations = new DebugStatTagInt("Num Iterations", 0, false);
		mDebugNumContacts = new DebugStatTagInt("Num Contacts", 0, false);

		Debug.debugManager().stats().addCustomStatTag(mDebugStatPhysicsCaption);
		Debug.debugManager().stats().addCustomStatTag(mDebugStatsNumBodies);
		Debug.debugManager().stats().addCustomStatTag(mDebugStepTimeInMm);
		Debug.debugManager().stats().addCustomStatTag(mDebugNumIterations);
		Debug.debugManager().stats().addCustomStatTag(mDebugNumContacts);

		mInitialized = true;
	}

	public void unload() {
		Debug.debugManager().stats().removeCustomStatTag(mDebugStatPhysicsCaption);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStatsNumBodies);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStepTimeInMm);
		Debug.debugManager().stats().removeCustomStatTag(mDebugNumIterations);
		Debug.debugManager().stats().removeCustomStatTag(mDebugNumContacts);

		mDebugStatPhysicsCaption = null;
		mDebugStatsNumBodies = null;
		mDebugStepTimeInMm = null;
		mDebugNumIterations = null;

		mInitialized = false;
	}

	public void step(float time, int iterations) {
		mActiveContactsManifoldsList.clear();

		if (mInitialized == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot step physics world - not initialized");
			return;
		}

		iterations = MathHelper.clampi(iterations, ConstantsPhysics.MIN_ITERATIONS, ConstantsPhysics.MAX_ITERATIONS);
		mDebugNumIterations.setValue(iterations);
		mDebugStatsNumBodies.setValue(numBodies());

		final var lSystemTimeBegin = System.nanoTime();

		time /= (float) iterations;
		for (int it = 0; it < iterations; it++) {

			// 1. Movement
			final int lNumBodies = mBodies.size();
			for (int i = 0; i < lNumBodies; i++) {
				mBodies.get(i).step(time, mGravityX, mGravityY);
			}

			var lManifold = getFreeContactManifold();

			// 2. Collision (Broad phase)
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

					// TODO: Move to narrow phase
					if (SAT.checkCollides(lBodyA, lBodyB, lManifold)) {

						separateBodiesByMTV(lBodyA, lBodyB, lManifold.mtv.x * lManifold.depth, lManifold.mtv.y * lManifold.depth);

						SAT.fillContactPoints(lManifold);

						mContacts.add(lManifold);
						lManifold = getFreeContactManifold();
					}
				}
			}

			mDebugNumContacts.setValue(mContacts.size());

			// 3. Resolution (Narrow Phase)
			final var lNumContacts = mContacts.size();
			for (int i = 0; i < lNumContacts; i++) {
				final var lNextCollision = mContacts.get(i);
				resolveCollision(lNextCollision.bodyA, lNextCollision.bodyB, lNextCollision);

				if (it == iterations - 1) {

					// Add contact points for debug display
					if (mActiveContactsManifoldsList.contains(lManifold) == false)
						mActiveContactsManifoldsList.add(lManifold);
				}
			}

			mContactPool.addAll(mContacts);
			mContacts.clear();
		}

		final var lDelta = ((System.nanoTime() - lSystemTimeBegin) / TimeConstants.NanoToMilli);
		mDebugStepTimeInMm.setValue((float) lDelta);
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

	private void resolveCollision(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final float relVelX = bodyB.vx - bodyA.vx;
		final float relVelY = bodyB.vy - bodyA.vy;

		final float dotVelNor = Vector2f.dot(relVelX, relVelY, manifold.mtv.x, manifold.mtv.y);

		if (dotVelNor > 0.f)
			return;

		final float minRestitution = Math.min(bodyA.restitution(), bodyB.restitution());
		float j = -(1.f + minRestitution) * dotVelNor;

		j /= (bodyA.invMass() + bodyB.invMass());

		final float impulseX = j * manifold.mtv.x;
		final float impulseY = j * manifold.mtv.y;

		bodyA.vx -= impulseX * bodyA.invMass();
		bodyA.vy -= impulseY * bodyA.invMass();

		bodyB.vx += impulseX * bodyB.invMass();
		bodyB.vy += impulseY * bodyB.invMass();
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

	public void createContacts(int amount) {
		amount = MathHelper.clampi(amount, 1, 128);
		for (int i = 0; i < amount; i++) {
			mContactPool.add(new ContactManifold());
		}
	}

	public ContactManifold getFreeContactManifold() {
		if (mContactPool.size() == 0)
			createContacts(16);

		return mContactPool.remove();
	}
}
