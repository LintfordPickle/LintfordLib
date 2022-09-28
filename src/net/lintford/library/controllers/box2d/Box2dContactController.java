package net.lintford.library.controllers.box2d;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;

public abstract class Box2dContactController extends BaseController implements ContactListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2d Contact Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected World mWorld;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dContactController(ControllerManager controllerManager, String controllerName, World box2dWorld, int entityUid) {
		super(controllerManager, controllerName, entityUid);

		mWorld = box2dWorld;
		mWorld.setContactListener(this);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unload() {
		mIsInitialized = false;
	}
}