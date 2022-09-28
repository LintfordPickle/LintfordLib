package net.lintford.library.controllers.camera;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.ICamera;

public class CameraController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mCamera;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	/** The associated {@link ICamera} object this controller controls. */
	public ICamera camera() {
		return mCamera;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraController(ControllerManager controllerManager, ICamera camera, int controllerBaseGroup) {
		super(controllerManager, CONTROLLER_NAME, controllerBaseGroup);

		mCamera = camera;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unload() {
		mCamera = null;
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (this.mCamera == null)
			return false;

		mCamera.handleInput(core);

		return super.handleInput(core);
	}

	/**
	 * Controls the zoom factor of the associated {@link Camera} object, if present and applicable.
	 */
	@Override
	public void update(LintfordCore core) {
		if (this.mCamera == null)
			return;

		mCamera.update(core);
	}
}