package net.lintfordlib.controllers.camera;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.camera.HUD;
import net.lintfordlib.core.camera.ICamera;

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