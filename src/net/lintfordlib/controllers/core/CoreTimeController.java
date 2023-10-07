package net.lintfordlib.controllers.core;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.LintfordCore.GameTime;

public class CoreTimeController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Core Time Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameTime mGameTime;
	private CoreTime mCoreTime;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameTime gameTime() {
		return mGameTime;
	}

	public CoreTime coreTime() {
		return mCoreTime;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CoreTimeController(final ControllerManager controllerManager, CoreTime coreTime, GameTime gameTime, int controllerGroupId) {
		super(controllerManager, CONTROLLER_NAME, controllerGroupId);

		mCoreTime = coreTime;
		mGameTime = gameTime;

		mIsInitialized = false;
	}
}