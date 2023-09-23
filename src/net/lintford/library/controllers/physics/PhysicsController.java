package net.lintford.library.controllers.physics;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.physics.PhysicsWorld;

public class PhysicsController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Physics Controller";

	public static final int NUM_PHYSICS_ITERATIONS = 7;

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

	public PhysicsController(ControllerManager controllerManager, IPhysicsControllerCallback callback, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mWorld = callback.createPhysicsWorld();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		mWorld.stepWorld((float) core.gameTime().elapsedTimeMilli() * 0.001f, NUM_PHYSICS_ITERATIONS);
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