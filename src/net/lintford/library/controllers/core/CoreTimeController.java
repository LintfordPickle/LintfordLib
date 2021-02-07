package net.lintford.library.controllers.core;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
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

	private boolean mIsInitialized;
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

	@Override
	public boolean isInitialized() {
		return mIsInitialized;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CoreTimeController(final ControllerManager pControllerManager, CoreTime pCoreTime, GameTime pGameTime, int pControllerGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroupID);

		mCoreTime = pCoreTime;
		mGameTime = pGameTime;

		mIsInitialized = false;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mIsInitialized = true;

	}

	@Override
	public void unload() {

	}

}
