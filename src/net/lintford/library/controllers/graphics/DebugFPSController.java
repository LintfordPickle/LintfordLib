package net.lintford.library.controllers.graphics;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.LintfordCore.CoreTime;

/** If enabled, prints the FPS to the console. */
public class DebugFPSController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "DebugFPSController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mFPSCounter;
	private int mFPS;
	private float mLastFPSTimer;
	private boolean mEnableFPSDisplay;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getFPS() {
		return mFPS;
	}

	@Override
	public boolean isinitialized() {
		return true;
	}

	public boolean enableFPSDisplay() {
		return mEnableFPSDisplay;
	}

	public void enableFPSDisplay(boolean pNewValue) {
		mEnableFPSDisplay = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugFPSController(final ControllerManager pControllerManager, final int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {

	}

	public void update(CoreTime pGameTime) {
		if (!mEnableFPSDisplay)
			return;

		if (pGameTime.totalAppTimeSeconds() - mLastFPSTimer > 1000) {
			mLastFPSTimer += 1000;
			mFPS = mFPSCounter;

			mFPSCounter = 0;

		}

		mFPSCounter++;

	}

}
