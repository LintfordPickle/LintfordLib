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

	public Box2dContactController(ControllerManager pControllerManager, String pControllerName, World pWorld, int pEntityID) {
		super(pControllerManager, pControllerName, pEntityID);

		mWorld = pWorld;
		mWorld.setContactListener(this);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean isinitialized() {
		return false;

	}

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
