package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.physics.PhysicsWorld;

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
	public PhysicsController(ControllerManager controllerManager, PhysicsWorld world, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mWorld = world;
	}

}