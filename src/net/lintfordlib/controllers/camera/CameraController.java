package net.lintfordlib.controllers.camera;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.camera.ICamera;

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
	public void unloadController() {
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