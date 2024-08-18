package net.lintfordlib.controllers.physics;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.physics.PhysicsWorld;

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
	private boolean mSimulationRunning;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void simulationRunning(boolean setSimulationRunning) {
		mSimulationRunning = setSimulationRunning;
	}

	public boolean isSimulationRunning() {
		return mSimulationRunning;
	}

	public PhysicsWorld world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsController(ControllerManager controllerManager, IPhysicsControllerCallback callback, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		if (callback != null) {
			mWorld = callback.createPhysicsWorld();
			mWorld.initialize();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void unloadController() {
		super.unloadController();

		if (mWorld != null) {
			mWorld.unload();
			mWorld = null;
		}
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (!mSimulationRunning)
			return;

		mWorld.stepWorld((float) core.gameTime().elapsedTimeMilli() * 0.001f, NUM_PHYSICS_ITERATIONS);
	}

	public void setPhysicsWorldGravity(float gx, float gy) {
		if (mWorld == null)
			return;

		mWorld.setGravity(gx, gy);
	}

}