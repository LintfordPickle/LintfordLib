package net.lintford.library.controllers.graphics;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore.CoreTime;

/** If enabled, prints the FPS to the console. */
public class DebugFPSController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Debug FPS Controller";

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

	public boolean enableFPSDisplay() {
		return mEnableFPSDisplay;
	}

	public void enableFPSDisplay(boolean isEnabled) {
		mEnableFPSDisplay = isEnabled;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugFPSController(final ControllerManager controllerManager, final int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unload() {

	}

	public void update(CoreTime coreTime) {
		if (!mEnableFPSDisplay)
			return;

		if (coreTime.totalTimeSeconds() - mLastFPSTimer > 1000) {
			mLastFPSTimer += 1000;
			mFPS = mFPSCounter;

			mFPSCounter = 0;
		}

		mFPSCounter++;
	}
}