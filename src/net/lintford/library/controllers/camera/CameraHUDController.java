package net.lintford.library.controllers.camera;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.camera.ICamera;

/** Controls the zoom factor of a {@link Camera} object. */
public class CameraHUDController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraHUDController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	/** The associated {@link Camera} object this controller should control. */
	private ICamera mCamera;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera camera() {
		return mCamera;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraHUDController(ControllerManager controllerManager, HUD hud, int controllerBaseGroup) {
		super(controllerManager, CONTROLLER_NAME, controllerBaseGroup);

		mCamera = hud;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unloadController() {
		mCamera = null;
	}
}