package net.lintfordlib.core.physics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.physics.collisions.ContactManifold;
import net.lintfordlib.core.physics.collisions.SATContacts;
import net.lintfordlib.core.physics.collisions.IntersectionTests;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.RigidBody.BodyType;
import net.lintfordlib.core.physics.interfaces.ICollisionCallback;
import net.lintfordlib.core.physics.resolvers.CollisionResolverRotationAndFriction;
import net.lintfordlib.core.physics.resolvers.CollisionResolverRotations;
import net.lintfordlib.core.physics.resolvers.CollisionResolverSimple;
import net.lintfordlib.core.physics.resolvers.ICollisionResolver;
import net.lintfordlib.core.physics.spatial.PhysicsHashGrid;
import net.lintfordlib.core.time.TimeConstants;

public class PhysicsWorld {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	private class CollisionPair {
		public RigidBody bodyA;
		public RigidBody bodyB;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final ContactManifold mContactManifold = new ContactManifold();

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mGravityX; // mps/s
	private float mGravityY; // mps/s

	private PhysicsHashGrid<RigidBody> mPhysicsHashGrid;
	private final List<RigidBody> mBodies = new ArrayList<>();

	private final LinkedList<CollisionPair> mCollisionPairPool = new LinkedList<>();
	private final List<CollisionPair> mCollisionPair = new ArrayList<>(16);
	private final List<ICollisionCallback> mCollisionCallbackList = new ArrayList<>();

	private ICollisionResolver mCollisionResolver;

	private boolean mAreBodiesLocked;
	private boolean mInitialized = false;
	private int updateCounter = 0;

	private final boolean enableMtvSeparation;
	private final boolean enableCollisionResponse;

	private int mCurrentIterationNr;
	private int mNumIterations;
	private double mStepTime;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/***
	 * @return Returns the current iteration number when queries from within the step method.
	 */
	public int currentIterationNr() {
		return mCurrentIterationNr;
	}

	/***
	 * @return The number of iterations of the step and collision checks to perform per step.
	 */
	public int numIterations() {
		return mNumIterations;
	}

	/***
	 * Sets the number of iterations of step and collision checking to perform during each step of the world. The higher the number, the more stable the simulation will be.
	 * 
	 * @param numIterations The number of iterations to set. Value will be clamped between {@link ConstantsPhysics.MIN_ITERATIONS} and {@link ConstantsPhysics.MAX_ITERATIONS}
	 */
	public void numIterations(int numIterations) {
		mNumIterations = MathHelper.clampi(numIterations, ConstantsPhysics.MIN_ITERATIONS, ConstantsPhysics.MAX_ITERATIONS);
	}

	/***
	 * @return The last step time (over all iterations) in ms.
	 */
	public double stepTime() {
		return mStepTime;
	}

	/***
	 * Bodies are locked during the step and broad phase of the world update and cannot be modified during this time.
	 * 
	 * @return Returns true if the bodies are currently locked, otherwise false.
	 */
	public boolean bodiesLocked() {
		return mAreBodiesLocked;
	}

	/***
	 * Sets the amount of gravity to be applied each frame.
	 * 
	 * @param x The x component of the desired gravity.
	 * @param y The y component of the desired gravity.
	 */
	public void setGravity(float x, float y) {
		mGravityX = x;
		mGravityY = y;
	}

	/***
	 * All {@link RigidBody} managed by the {@link PhysicsWorld} are held within the {@link PhysicsHashGrid}.
	 * 
	 * @return The current instance of {@link PhysicsHashGrid} managing the {@link PhysicsWorld}'s {@link RigidBody}s.
	 */
	public PhysicsHashGrid<RigidBody> grid() {
		return mPhysicsHashGrid;
	}

	/***
	 * 
	 * @return A list of all {@link RigidBody}s currently managed by the {@link PhysicsWorld}.
	 */
	public List<RigidBody> bodies() {
		return mBodies;
	}

	/***
	 * @return Returns the number of {@link RigidBody} instances currently being managed by this {@link PhysicsWorld}.
	 */
	public int numBodies() {
		return mBodies.size();
	}

	/***
	 * Adds an instance of {@link ICollisionCallback} to the world for automatical callbacks. An instance can only be added once.
	 * 
	 * @param callback The instance to add.
	 */
	public void addCollisionCallback(ICollisionCallback callback) {
		if (!mCollisionCallbackList.contains(callback))
			mCollisionCallbackList.add(callback);
	}

	/***
	 * Removes a previously added instance of {@link ICollisionCallback}.
	 * 
	 * @param callback The instance of {@link ICollisionCallback} to remove.
	 */
	public void removeCollisionCallback(ICollisionCallback callback) {
		if (mCollisionCallbackList.contains(callback))
			mCollisionCallbackList.remove(callback);
	}

	/***
	 * Sets the collision resolver to be used when resolving collisions in the world step. There can only be one instance of {@link ICollisionResolver} used at a time.
	 * 
	 * @param resolver An instance of {@link ICollisionResolver} to use for collision resolution.
	 * @see {@link CollisionResolverSimple}, {@link CollisionResolverRotations} and {@link CollisionResolverRotationAndFriction}.
	 */
	public void setContactResolver(ICollisionResolver resolver) {
		mCollisionResolver = resolver;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsWorld() {
		this(null);
	}

	public PhysicsWorld(PhysicsSettings settings) {
		if (settings == null)
			settings = PhysicsSettings.DefaultSettings;

		mPhysicsHashGrid = new PhysicsHashGrid<>(settings.hashGridWidthInUnits, settings.hashGridHeightInUnits, settings.hashGridCellsWide, settings.hashGridCellsHigh);

		enableMtvSeparation = settings.enable_mtv_separation;
		enableCollisionResponse = settings.enable_collision_resolver;

		mCollisionResolver = new CollisionResolverSimple();

		setGravity(settings.gravityX, settings.gravityY);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		if (mInitialized) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot initialize the PhysicsController multiple times.");
			return;
		}

		initializeCollisionPairPool();

		mInitialized = true;
	}

	private void initializeCollisionPairPool() {
		for (int i = 0; i < 32; i++) {
			mCollisionPairPool.addLast(new CollisionPair());
		}
	}

	public void unload() {
		mInitialized = false;

		final int lNumObjectsInCollisionPool = mCollisionPairPool.size();
		for (int i = 0; i < lNumObjectsInCollisionPool; i++) {
			final var lCollisionPair = mCollisionPairPool.get(i);

			if (lCollisionPair != null) {
				lCollisionPair.bodyA = null;
				lCollisionPair.bodyB = null;
			}
		}
		mCollisionPairPool.clear();

		final var lNumBodies = mBodies.size();
		for (int i = 0; i < lNumBodies; i++) {
			final var lBody = mBodies.get(i);
			if (lBody != null) {
				lBody.userData(null);
			}
		}
		mBodies.clear();

		mCollisionCallbackList.clear();
	}

	public void stepWorld(float time, int totalIterations) {
		if (!mInitialized) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot step physics world - not initialized");
			return;
		}

		if (mCollisionResolver == null)
			return;

		final var lSystemTimeBegin = System.nanoTime();

		time /= totalIterations;
		for (int it = 0; it < totalIterations; it++) {
			mCurrentIterationNr = it;

			mAreBodiesLocked = true;
			mCollisionPair.clear();
			stepBodies(time);

			broadPhase();
			mAreBodiesLocked = false;

			narrowPhase();
		}
		mCurrentIterationNr = -1;

		mStepTime = ((System.nanoTime() - lSystemTimeBegin) / TimeConstants.NanoToMilli);
	}

	private void stepBodies(float time) {
		updateCounter++;

		final var lActiveCellKeys = mPhysicsHashGrid.getActiveCellKeys();
		final int lNumActiveCellKeys = lActiveCellKeys.size();
		for (int i = 0; i < lNumActiveCellKeys; i++) {
			final var lCellKey = lActiveCellKeys.get(i);
			final var lCell = mPhysicsHashGrid.getCell(lCellKey);
			final var lNumEntitiesInCell = lCell.size();

			boolean isCellActive = false;
			for (int j = lNumEntitiesInCell - 1; j >= 0; j--) {
				final var lBody = lCell.get(j);

				if (lBody._updateCounter >= updateCounter)
					continue;

				lBody._updateCounter = updateCounter;
				lBody.step(time, mGravityX, mGravityY);

				mPhysicsHashGrid.updateEntity(lBody);

				isCellActive = isCellActive || lBody._isActive;
			}
		}
	}

	private void broadPhase() {
		final var lActiveCellKeys = mPhysicsHashGrid.getActiveCellKeys();
		final int lNumActiveCellKeys = lActiveCellKeys.size();
		for (int i = lNumActiveCellKeys - 1; i >= 0; i--) {
			final var lCellKey = lActiveCellKeys.get(i);
			final var lCell = mPhysicsHashGrid.getCell(lCellKey);
			final var lNumEntitiesInCell = lCell.size();

			if (lNumEntitiesInCell == 0) {
				lActiveCellKeys.remove(i);
				continue;
			}

			for (int j = 0; j < lNumEntitiesInCell; j++) {
				final var lBodyA = lCell.get(j);
				final var lBodyA_aabb = lBodyA.aabb();

				lBodyA.debugIsColliding = false;

				for (int s = j + 1; s < lNumEntitiesInCell; s++) {
					final var lBodyB = lCell.get(s);

					if (lBodyA == lBodyB)
						continue;

					if (lBodyA.bodyType() == BodyType.Static && lBodyB.bodyType() == BodyType.Static)
						continue;

					if (lBodyA.categoryBits() == 0 || lBodyB.categoryBits() == 0)
						continue;

					if (lBodyA.maskBits() == 0 || lBodyB.maskBits() == 0)
						continue;

					final var passedFilterCollision = (lBodyA.maskBits() & lBodyB.categoryBits()) != 0 && (lBodyA.categoryBits() & lBodyB.maskBits()) != 0;

					if (!passedFilterCollision)
						continue;

					final var lBodyB_aabb = lBodyB.aabb();
					if (!lBodyA_aabb.intersectsAA(lBodyB_aabb))
						continue;

					final var lCollisionPair = getFreeCollisionPair();
					lCollisionPair.bodyA = lBodyA;
					lCollisionPair.bodyB = lBodyB;

					mCollisionPair.add(lCollisionPair);
				}
			}
		}
	}

	private void narrowPhase() {
		final var lNumCallbacks = mCollisionCallbackList.size();

		final var lNumCollisionPairs = mCollisionPair.size();
		for (int i = 0; i < lNumCollisionPairs; i++) {
			final var lCollisionPair = mCollisionPair.get(i);

			mContactManifold.initialize(lCollisionPair.bodyA, lCollisionPair.bodyB);

			final var lBodyA = lCollisionPair.bodyA;
			final var lBodyB = lCollisionPair.bodyB;

			if (IntersectionTests.checkCollides(mContactManifold)) {
				lBodyA.debugIsColliding = true;
				lBodyB.debugIsColliding = true;

				for (int j = 0; j < lNumCallbacks; j++)
					mCollisionCallbackList.get(j).preContact(mContactManifold);

				if (!mContactManifold.enableResolveContact) {
					returnCollisionPair(lCollisionPair);
					continue;
				}

				final var lDealingWithSensorShape = lBodyA.isSensor() || lBodyB.isSensor();
				if (enableMtvSeparation && !lDealingWithSensorShape)
					separateBodiesByMTV(mContactManifold);

				SATContacts.fillContactPoints(mContactManifold);

				for (int j = 0; j < lNumCallbacks; j++)
					mCollisionCallbackList.get(j).postContact(mContactManifold);

				if (enableCollisionResponse && mCollisionResolver != null) {
					for (int j = 0; j < lNumCallbacks; j++)
						mCollisionCallbackList.get(j).preSolve(mContactManifold);

					if (!lDealingWithSensorShape)
						mCollisionResolver.resolveCollisions(mContactManifold);

					for (int j = 0; j < lNumCallbacks; j++)
						mCollisionCallbackList.get(j).postSolve(mContactManifold);

				}
			}

			returnCollisionPair(lCollisionPair);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void separateBodiesByMTV(final ContactManifold contact) {
		final var lBodyA = contact.bodyA;
		final var lBodyB = contact.bodyB;

		// The Vector 'mtv' is the direction to push BodyB outside of BodyA
		final var lMtvX = contact.normal.x * contact.depth;
		final var lMtvY = contact.normal.y * contact.depth;

		if (lBodyA.bodyType() == BodyType.Static || lBodyA.bodyType() == BodyType.Kenetic) {
			lBodyB.transform.p.x += lMtvX;
			lBodyB.transform.p.y += lMtvY;

		} else if (lBodyB.bodyType() == BodyType.Static || lBodyB.bodyType() == BodyType.Dynamic) {
			lBodyA.transform.p.x -= lMtvX;
			lBodyA.transform.p.y -= lMtvY;

		} else {
			lBodyA.transform.p.x += -lMtvX / 2.f;
			lBodyA.transform.p.y += -lMtvY / 2.f;

			lBodyB.transform.p.x += lMtvX / 2.f;
			lBodyB.transform.p.y += lMtvY / 2.f;
		}
	}

	public void addBody(RigidBody newBody) {
		mPhysicsHashGrid.addEntity(newBody);

		mBodies.add(newBody);
	}

	public boolean removeBody(RigidBody body) {
		if (mAreBodiesLocked) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot remove bodies from the physics world while the _LockedBodies flag is set");
			return false;
		}

		mPhysicsHashGrid.removeEntity(body);
		return mBodies.remove(body);
	}

	public RigidBody getBodyByIndex(int bodyIndex) {
		if (bodyIndex < 0 || bodyIndex >= mBodies.size())
			return null;

		return mBodies.get(bodyIndex);
	}

	private CollisionPair getFreeCollisionPair() {
		if (!mCollisionPairPool.isEmpty()) {
			return mCollisionPairPool.removeFirst();
		}

		enlargeCollisionPairPool(8);
		return mCollisionPairPool.removeFirst();
	}

	// -----

	private void enlargeCollisionPairPool(final int amount) {
		for (int i = 0; i < amount; i++) {
			mCollisionPairPool.addLast(new CollisionPair());
		}
	}

	private void returnCollisionPair(CollisionPair obj) {
		obj.bodyA = null;
		obj.bodyB = null;
		mCollisionPairPool.addLast(obj);
	}

}
