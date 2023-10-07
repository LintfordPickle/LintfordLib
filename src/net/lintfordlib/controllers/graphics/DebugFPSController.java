package net.lintfordlib.controllers.graphics;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore.CoreTime;

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