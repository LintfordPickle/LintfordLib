package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.physics.PhysicsWorld;
import net.lintford.library.core.physics.resolvers.CollisionResolverRotationAndFriction;

public class PhysicsController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Physics Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsWorld mWorld;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PhysicsWorld world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------
	public PhysicsController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public PhysicsWorld createNewPhyicsWorld(int boundaryWidth, int boundaryHeight, int numTilesWide, int numTilesHigh) {
		if (boundaryWidth <= 0)
			boundaryWidth = 400;

		if (boundaryHeight <= 0)
			boundaryHeight = 400;

		if (numTilesWide <= 10)
			numTilesWide = 10;

		if (numTilesHigh <= 10)
			numTilesHigh = 10;

		mWorld = new PhysicsWorld(boundaryWidth, boundaryHeight, numTilesWide, numTilesHigh);
		mWorld.setContactResolver(new CollisionResolverRotationAndFriction());

		mWorld.initialize();
		return mWorld;
	}

	@Override
	public void unloadController() {
		super.unloadController();

		mWorld.unload();
		mWorld = null;
	}

	public void setPhysicsWorldGravity(float gx, float gy) {
		if (mWorld == null)
			return;

		mWorld.setGravity(gx, gy);
	}

}