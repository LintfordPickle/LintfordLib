package net.lintford.library.controllers.box2d;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public abstract class Box2dContactController extends BaseController implements ContactListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2dContactController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private World mWorld;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dContactController(ControllerManager pControllerManager, String pControllerName, World pWorld, int pEntityID) {
		super(pControllerManager, pControllerName, pEntityID);

		mWorld = pWorld;
		mWorld.setContactListener(this);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		return false;

	}

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mWorld != null) {
			mWorld.step((float) pCore.time().elapseGameTimeSeconds(), 5, 6);

		}

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
