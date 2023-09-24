package net.lintford.library.core.physics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStatTagCaption;
import net.lintford.library.core.debug.stats.DebugStatTagFloat;
import net.lintford.library.core.debug.stats.DebugStatTagInt;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.physics.collisions.ContactManifold;
import net.lintford.library.core.physics.collisions.SAT;
import net.lintford.library.core.physics.dynamics.RigidBody;
import net.lintford.library.core.physics.interfaces.ICollisionCallback;
import net.lintford.library.core.physics.resolvers.ICollisionResolver;
import net.lintford.library.core.physics.spatial.PhysicsHashGrid;
import net.lintford.library.core.time.TimeConstants;

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

	private float mGravityX;
	private float mGravityY; // mps/s

	private PhysicsHashGrid<RigidBody> mWorldHashGrid;
	private final List<RigidBody> mBodies = new ArrayList<>();

	private final LinkedList<CollisionPair> mCollisionPairPool = new LinkedList<>();
	private final List<CollisionPair> mCollisionPair = new ArrayList<>(16);
	private final List<ICollisionCallback> mCollisionCallbackList = new ArrayList<>();

	private ICollisionResolver mCollisionResolver;

	// Debug
	private DebugStatTagCaption mDebugStatPhysicsCaption;
	private DebugStatTagInt mDebugStatsNumBodies;
	private DebugStatTagFloat mDebugStepTimeInMm;
	private DebugStatTagInt mDebugNumIterations;
	private DebugStatTagInt mNumSpatialCells;
	private DebugStatTagInt mNumActiveCells;

	private boolean _lockedBodies;
	private boolean mInitialized = false;
	private int updateCounter = 0;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Bodies are locked durin the step and broad phase and cannot be removed from the world during this time. */
	public boolean bodiesLocked() {
		return _lockedBodies;
	}

	public void setGravity(float x, float y) {
		mGravityX = x;
		mGravityY = y;
	}

	public PhysicsHashGrid<RigidBody> grid() {
		return mWorldHashGrid;
	}

	public List<RigidBody> bodies() {
		return mBodies;
	}

	public void addCollisionCallback(ICollisionCallback callback) {
		if (mCollisionCallbackList.contains(callback) == false)
			mCollisionCallbackList.add(callback);
	}

	public void removeCollisionCallback(ICollisionCallback callback) {
		if (mCollisionCallbackList.contains(callback))
			mCollisionCallbackList.remove(callback);
	}

	public void setContactResolver(ICollisionResolver resolver) {
		mCollisionResolver = resolver;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsWorld() {
		this(100, 100, 10, 10);
	}

	public PhysicsWorld(int fieldWidth, int fieldHeight, int numCellsWide, int numCellsHigh) {
		mWorldHashGrid = new PhysicsHashGrid<>(fieldWidth, fieldHeight, numCellsWide, numCellsHigh);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		initializeCollisionPairPool();

		// TODO: Only do this if in debug mode
		if (Debug.debugManager().debugModeEnabled()) {
			mDebugStatPhysicsCaption = new DebugStatTagCaption("Physics");
			mDebugStatsNumBodies = new DebugStatTagInt("Num Bodies", 0, false);
			mDebugStepTimeInMm = new DebugStatTagFloat("step", 0.0f, false);
			mDebugNumIterations = new DebugStatTagInt("Num Iterations", 0, false);
			mNumSpatialCells = new DebugStatTagInt("Num Cells", 0, false);
			mNumActiveCells = new DebugStatTagInt("Active Cells", 0, false);

			Debug.debugManager().stats().addCustomStatTag(mDebugStatPhysicsCaption);
			Debug.debugManager().stats().addCustomStatTag(mDebugStatsNumBodies);
			Debug.debugManager().stats().addCustomStatTag(mDebugStepTimeInMm);
			Debug.debugManager().stats().addCustomStatTag(mDebugNumIterations);
			Debug.debugManager().stats().addCustomStatTag(mNumSpatialCells);
			Debug.debugManager().stats().addCustomStatTag(mNumActiveCells);
		}

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
		Debug.debugManager().stats().removeCustomStatTag(mNumSpatialCells);
		Debug.debugManager().stats().removeCustomStatTag(mNumActiveCells);

		mDebugStatPhysicsCaption = null;
		mDebugStatsNumBodies = null;
		mDebugStepTimeInMm = null;
		mDebugNumIterations = null;
		mNumSpatialCells = null;
		mNumActiveCells = null;

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
		if (mInitialized == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot step physics world - not initialized");
			return;
		}

		final var lSystemTimeBegin = System.nanoTime();

		time /= (float) totalIterations;
		for (int it = 0; it < totalIterations; it++) {

			_lockedBodies = true;
			mCollisionPair.clear();
			stepBodies(time);

			broadPhase();
			_lockedBodies = false;

			narrowPhase();
		}

		final var lDelta = ((System.nanoTime() - lSystemTimeBegin) / TimeConstants.NanoToMilli);
		updateDebugStats(totalIterations, lDelta);

	}

	private void updateDebugStats(int totalIterations, double delta) {
		if (Debug.debugManager().debugModeEnabled() == false)
			return;

		totalIterations = MathHelper.clampi(totalIterations, ConstantsPhysics.MIN_ITERATIONS, ConstantsPhysics.MAX_ITERATIONS);
		mDebugNumIterations.setValue(totalIterations);
		mDebugStatsNumBodies.setValue(mBodies.size());
		mNumSpatialCells.setValue(mWorldHashGrid.getTotalCellCount());
		mNumActiveCells.setValue(mWorldHashGrid.getActiveCellKeys().size());
		mDebugStepTimeInMm.setValue((float) delta);
	}

	private void stepBodies(float time) {
		updateCounter++;

		final var lActiveCellKeys = mWorldHashGrid.getActiveCellKeys();
		final int lNumActiveCellKeys = lActiveCellKeys.size();
		for (int i = 0; i < lNumActiveCellKeys; i++) {
			final var lCellKey = lActiveCellKeys.get(i);
			final var lCell = mWorldHashGrid.getCell(lCellKey);
			final var lNumEntitiesInCell = lCell.size();

			boolean isCellActive = false;
			for (int j = lNumEntitiesInCell - 1; j >= 0; j--) {
				final var lBody = lCell.get(j);

				if (lBody._updateCounter >= updateCounter)
					continue;

				lBody._updateCounter = updateCounter;
				lBody.step(time, mGravityX, mGravityY);

				mWorldHashGrid.updateEntity(lBody);

				isCellActive = isCellActive || lBody._isActive;
			}
		}
	}

	private void broadPhase() {
		// TODO: Broad phrase double adds entities to the collision pairs if they cross grid boundaries.
		final var lActiveCellKeys = mWorldHashGrid.getActiveCellKeys();
		final int lNumActiveCellKeys = lActiveCellKeys.size();
		for (int i = lNumActiveCellKeys - 1; i >= 0; i--) {
			final var lCellKey = lActiveCellKeys.get(i);
			final var lCell = mWorldHashGrid.getCell(lCellKey);
			final var lNumEntitiesInCell = lCell.size();

			if (lNumEntitiesInCell == 0) {
				lActiveCellKeys.remove(i);
				continue;
			}

			for (int j = 0; j < lNumEntitiesInCell; j++) {
				final var lBodyA = lCell.get(j);
				final var lBodyA_aabb = lBodyA.aabb();

				for (int s = j + 1; s < lNumEntitiesInCell; s++) {
					final var lBodyB = lCell.get(s);

					if (lBodyA == lBodyB)
						continue;

					final var lBodyB_aabb = lBodyB.aabb();

					if (lBodyA.isStatic() && lBodyB.isStatic())
						continue;

					if (lBodyA_aabb.intersectsAA(lBodyB_aabb) == false)
						continue;

					final var lWeAreIncludedinPhysics = lBodyA.maskBits() != 0 && lBodyB.maskBits() != 0;
					if (lWeAreIncludedinPhysics == false)
						continue;

					final var lWeBothCollideWithOthers = lBodyA.categoryBits() != 0 && lBodyB.categoryBits() != 0;

					final var passedFilterCollision = lWeBothCollideWithOthers == false || (lBodyA.maskBits() & lBodyB.categoryBits()) != 0 && (lBodyA.categoryBits() & lBodyB.maskBits()) != 0;

					if (!passedFilterCollision) {
						continue;
					}

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

			final var lBodyA = lCollisionPair.bodyA;
			final var lBodyB = lCollisionPair.bodyB;

			mContactManifold.reset();

			if (SAT.checkCollides(lBodyA, lBodyB, mContactManifold)) {

				final var includeFilter = lBodyA.maskBits() != 0 && lBodyB.maskBits() != 0 && lBodyA.categoryBits() != 0 && lBodyB.categoryBits() != 0;
				final var passedFilterCollision = !includeFilter || (lBodyA.maskBits() & lBodyB.categoryBits()) != 0 && (lBodyA.categoryBits() & lBodyB.maskBits()) != 0;

				if (!passedFilterCollision) {

					// TODO: Handle the case of sensor bodies
					returnCollisionPair(lCollisionPair);
					break;
				}

				for (int j = 0; j < lNumCallbacks; j++) {
					mCollisionCallbackList.get(j).preContact(mContactManifold);
				}

				if (mContactManifold.enableResolveContact == false) {
					returnCollisionPair(lCollisionPair);
					continue;
				}

				separateBodiesByMTV(lBodyA, lBodyB, mContactManifold.normal.x * mContactManifold.depth, mContactManifold.normal.y * mContactManifold.depth);

				SAT.fillContactPoints(mContactManifold);

				for (int j = 0; j < lNumCallbacks; j++) {
					mCollisionCallbackList.get(j).postContact(mContactManifold);
				}

				if (mCollisionResolver != null) {
					for (int j = 0; j < lNumCallbacks; j++) {
						mCollisionCallbackList.get(j).preSolve(mContactManifold);
					}

					mCollisionResolver.resolveCollisions(mContactManifold);

					for (int j = 0; j < lNumCallbacks; j++) {
						mCollisionCallbackList.get(j).postSolve(mContactManifold);
					}
				}
			}

			returnCollisionPair(lCollisionPair);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// TODO: Move this to the collision package
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
		mWorldHashGrid.addEntity(newBody);

		mBodies.add(newBody);
	}

	public boolean removeBody(RigidBody body) {
		if (_lockedBodies) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot remove bodies from the physics world while the _LockedBodies flag is set");
			return false;
		}

		mWorldHashGrid.removeEntity(body);
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
