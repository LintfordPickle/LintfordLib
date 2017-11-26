package net.lintford.library.controllers.camera;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.time.GameTime;

/** Controls the zoom factor of a {@link Camera} object. */
public class CameraController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraController";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	/** The associated {@link Camera} object this controller should control. */
	private Camera mCamera;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialised() {
		return mCamera != null;
	}

	@Override
	public void initialise() {

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/** Ctor. */
	public CameraController(ControllerManager pControllerManager, Camera pCamera, int pControllerBaseGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerBaseGroup);

		mCamera = pCamera;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Controls the zoom factor of the associated {@link Camera} object, if present and applicable.
	 */
	public void update(final GameTime pGameTime) {
		if (this.mCamera == null)
			return;

		// Apply the new zoom factor to the camera object
		mCamera.update(pGameTime);

	}

}
