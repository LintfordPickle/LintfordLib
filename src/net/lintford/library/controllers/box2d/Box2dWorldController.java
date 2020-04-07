package net.lintford.library.controllers.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.maths.RandomNumbers;

public class Box2dWorldController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2dWorldController";

	public static final float UNITS_TO_PIXELS = 32f;
	public static final float PIXELS_TO_UNITS = 1f / UNITS_TO_PIXELS;

	public static final int CATEGORY_CHARACTER = 0b00000001;
	public static final int CATEGORY_ENEMY     = 0b00000010;
	public static final int CATEGORY_WEAPON    = 0b00000100;
	public static final int CATEGORY_ITEM      = 0b00001000;
	public static final int CATEGORY_OBJECT    = 0b00010000;
	public static final int CATEGORY_GROUND    = 0b00100000;
	public static final int CATEGORY_NOTHING   = 0b01000000;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ResourceController mResourceController;
	protected World mWorld;

	float mWindAcc;
	float mWindVel;

	public boolean isPaused;

	// TODO: Create a Body Pool and pre-allocation (for CollisionNodes)

	// --------------------------------------
	// Properties
	// --------------------------------------

	public World world() {
		return mWorld;
	}

	@Override
	public boolean isinitialized() {
		return mWorld != null;

	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dWorldController(ControllerManager pControllerManager, World pWorld, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mWorld = pWorld;
		isPaused = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mResourceController = (ResourceController) pCore.controllerManager().getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		// TODO: WindController
		final float lWindMaxForce = 0.1f;
		mWindAcc = RandomNumbers.getRandomChance(10) ? RandomNumbers.random(-lWindMaxForce, lWindMaxForce) : 0;
		mWindVel += mWindAcc;
		mWindVel *= 0.99f;
		mWindAcc = 0;

		// mWorld.setGravity(new Vec2(mWindVel, 9.8f));

		if (mWorld != null && !isPaused) {
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

	public void returnBox2dInstance(JBox2dEntityInstance pObjectToRetrun) {
		if (pObjectToRetrun == null)
			return;

		pObjectToRetrun.unloadPhysics();
		
	}

}
