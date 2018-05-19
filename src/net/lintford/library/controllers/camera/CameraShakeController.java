package net.lintford.library.controllers.camera;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ShakeCamera;

public class CameraShakeController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraShakeController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ShakeCamera mGameCamera;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ShakeCamera gameCamera() {
		return mGameCamera;
	}

	@Override
	public boolean isInitialised() {
		return mGameCamera != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraShakeController(ControllerManager pControllerManager, ShakeCamera pCamera, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		//
		mGameCamera = pCamera;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		if (pCore.input().keyDown(GLFW.GLFW_KEY_U)) {
			mGameCamera.shake(100f, 20f);

		}

		return false;

	}

	@Override
	public void initialise(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		mGameCamera = null;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void shake(float pDurMS, float pMag) {
		mGameCamera.shake(pDurMS, pMag);

	}

}
