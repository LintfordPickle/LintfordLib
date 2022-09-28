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

	public static final float UPDATE_TIMER = (1.f / 60.f);

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ResourceController mResourceController;
	protected World mWorld;
	protected boolean mIsSingleStep;
	protected boolean mIsPaused;
	protected int mLogicalStepCounter;
	protected int mVelocityIterations;
	protected int mPositionIterations;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int velocityIterations() {
		return mVelocityIterations;
	}

	public void velocityIterations(int velocityIterationCount) {
		velocityIterationCount = MathHelper.clampi(velocityIterationCount, 1, 50);
		mVelocityIterations = velocityIterationCount;
	}

	public int positionIterations() {
		return mPositionIterations;
	}

	public void positionIterations(int positionIterationCount) {
		positionIterationCount = MathHelper.clampi(positionIterationCount, 1, 50);
		mPositionIterations = positionIterationCount;
	}

	public int logicalStepCounter() {
		return mLogicalStepCounter;
	}

	public World world() {
		return mWorld;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Box2dWorldController(ControllerManager controllerManager, World box2dWorld, int pEntityGroupUid) {
		this(controllerManager, CONTROLLER_NAME, box2dWorld, pEntityGroupUid);
	}

	public Box2dWorldController(ControllerManager controllerManager, String controllerNamer, World box2dWorld, int entityGroupUid) {
		super(controllerManager, controllerNamer, entityGroupUid);

		mVelocityIterations = 8;
		mPositionIterations = 3;

		mWorld = box2dWorld;
		mIsPaused = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		mResourceController = (ResourceController) core.controllerManager().getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mIsSingleStep) {
			stepWorld(core);

			mIsSingleStep = false;
			mIsPaused = true;

			return;
		}

		if (mIsPaused)
			return;

		if (mWorld != null && !core.gameTime().isTimePaused())
			stepWorld(core);
	}

	private void stepWorld(LintfordCore core) {
		mLogicalStepCounter++;

		mWorld.step(UPDATE_TIMER * core.gameTime().timeModifier(), mVelocityIterations, mPositionIterations);
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void returnBox2dInstance(JBox2dEntityInstance box2dEntityInstanceToReturn) {
		if (box2dEntityInstanceToReturn == null)
			return;

		if (box2dEntityInstanceToReturn.userDataObject() != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "JBox2dEntityInstance unloaded without first removing the userdata object. typeof (" + box2dEntityInstanceToReturn.userDataObject().toString() + ")");
			box2dEntityInstanceToReturn.userDataObject(null);
		}

		box2dEntityInstanceToReturn.unloadPhysics();
		final var lResourceManager = mResourceController.resourceManager();

		lResourceManager.pobjectManager().returnPooledItem(box2dEntityInstanceToReturn);
	}

	public void reset() {
		mLogicalStepCounter = 0;
		mIsSingleStep = false;
		mIsPaused = true;
	}

	public void pause() {
		mIsSingleStep = false;
		mIsPaused = true;
	}

	public void singleStep() {
		mIsSingleStep = true;
		mIsPaused = false;
	}

	public void play() {
		mIsSingleStep = false;
		mIsPaused = false;
	}

}
