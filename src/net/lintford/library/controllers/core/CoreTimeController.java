package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.LintfordCore.GameTime;

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

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unload() {

	}
}