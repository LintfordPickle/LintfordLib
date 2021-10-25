package net.lintford.library.controllers.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.maths.MathHelper;

public class Box2dWorldController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Box2dWorldController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ResourceController mResourceController;
	protected World mWorld;

	protected boolean singleStep;
	protected boolean isPaused;
	protected int mLogicalStepCounter;

	protected int mVelocityIterations;
	protected int mPositionIterations;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int velocityIterations() {
		return mVelocityIterations;
	}

	public void velocityIterations(int pNewVelocityIterationCount) {
		pNewVelocityIterationCount = MathHelper.clampi(pNewVelocityIterationCount, 1, 50);
		mVelocityIterations = pNewVelocityIterationCount;
	}

	public int positionIterations() {
		return mPositionIterations;
	}

	public void positionIterations(int pNewPositionIterationCount) {
		pNewPositionIterationCount = MathHelper.clampi(pNewPositionIterationCount, 1, 50);
		mPositionIterations = pNewPositionIterationCount;
	}

	public int logicalStepCounter() {
		return mLogicalStepCounter;
	}

	public World world() {
		return mWorld;
	}

	@Override
	public boolean isInitialized() {
		return mResourceController != null && mWorld != null;

	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dWorldController(ControllerManager pControllerManager, World pWorld, int pEntityGroupID) {
		this(pControllerManager, CONTROLLER_NAME, pWorld, pEntityGroupID);

	}

	public Box2dWorldController(ControllerManager pControllerManager, String pControllerNamer, World pWorld, int pEntityGroupID) {
		super(pControllerManager, pControllerNamer, pEntityGroupID);

		mVelocityIterations = 8;
		mPositionIterations = 3;

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

		if (singleStep) {
			stepWorld(pCore);

			singleStep = false;
			isPaused = true;

			return;
		}

		if (isPaused) {
			return;

		}

		if (mWorld != null && !pCore.gameTime().isTimePaused()) {
			stepWorld(pCore);

		}

	}

	private void stepWorld(LintfordCore pCore) {
		mLogicalStepCounter++;

		mWorld.step((1f / 60f) * pCore.gameTime().timeModifier(), mVelocityIterations, mPositionIterations);

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void returnBox2dInstance(JBox2dEntityInstance pJBox2dEntityInstanceToReturn) {
		if (pJBox2dEntityInstanceToReturn == null)
			return;

		if (pJBox2dEntityInstanceToReturn.userDataObject() != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "JBox2dEntityInstance unloaded without first removing the userdata object. typeof (" + pJBox2dEntityInstanceToReturn.userDataObject().toString() + ")");
			pJBox2dEntityInstanceToReturn.userDataObject(null);

		}

		pJBox2dEntityInstanceToReturn.unloadPhysics();
		final var lResourceManager = mResourceController.resourceManager();

		lResourceManager.pobjectManager().returnPooledItem(pJBox2dEntityInstanceToReturn);

	}

	public void reset() {
		mLogicalStepCounter = 0;
		singleStep = false;
		isPaused = true;
	}

	public void pause() {
		singleStep = false;
		isPaused = true;
	}

	public void singleStep() {
		singleStep = true;
		isPaused = false;
	}

	public void play() {
		singleStep = false;
		isPaused = false;
	}

}
