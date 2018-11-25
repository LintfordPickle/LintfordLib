package net.lintford.library.controllers.box2d;

import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class Box2dWorldController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2dWorldController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private World mWorld;
	private List<Body> mBodyPool;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dWorldController(ControllerManager pControllerManager, World pWorld, int pEntityID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityID);

		mWorld = pWorld;

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
