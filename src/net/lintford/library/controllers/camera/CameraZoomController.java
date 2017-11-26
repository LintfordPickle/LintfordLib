package net.lintford.library.controllers.camera;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.time.GameTime;

/** Controls the zoom factor of a {@link Camera} object. */
public class CameraZoomController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraZoomController";

	/** Specifies the minimum amount of camera zoom */
	public static final float MIN_CAMERA_ZOOM = 0.75f;

	/** Specifies the maximum amount of camera zoom */
	public static final float MAX_CAMERA_ZOOM = 1.4f;

	/**
	 * Specifies the amount of DRAG to be applied to the zoom factor velocity over time.
	 */
	public static final float ZOOM_VELOCITY_DRAG = 0.8f;

	/**
	 * A coefficient for the speed of the zoom (modifies the mouse scroll wheel speed)
	 */
	public static final float ZOOM_ACCELERATE_AMOUNT = 50.0f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	/** The associated {@link Camera} object this controller should control. */
	private Camera mCamera;

	/** tracks the state of the velocity and accelerate over time. */
	private float mZoomAcceleration;

	/** tracks the state of the velocity and accelerate over time. */
	private float mZoomVelocity;

	/** Flag to enable/disable zoom control in this controller. */
	private boolean mAllowZoom = true;

	/** Smaller is further out */
	private float mCameraMinZoom;

	/** Larger is more zoomed in */
	private float mCameraMaxZoom;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	/**
	 * Sets the {@link Camera} object this controller works with. If null, the controller will skip its update calls.
	 */
	public void setCamera(Camera pCamera) {
		this.mCamera = pCamera;
	}

	public void setZoomConstraints(float pMin, float pMax) {
		mCameraMinZoom = pMin;

		if (pMin < MIN_CAMERA_ZOOM) {
			pMin = MIN_CAMERA_ZOOM;
		}

		if (pMax > MAX_CAMERA_ZOOM) {
			pMax = MAX_CAMERA_ZOOM;
		}

		if (pMin > pMax) {
			// Just crazy talk
			pMin = pMax = mCameraMaxZoom = 1f;

		} else {
			mCameraMinZoom = pMin;
			mCameraMaxZoom = pMax;

		}

	}

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
	public CameraZoomController(ControllerManager pControllerManager, Camera pCamera, int pControllerBaseGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerBaseGroup);

		mCamera = pCamera;
		this.mCameraMinZoom = mCameraMaxZoom = 1.0f;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Listens to mouse scroll wheel input and updates the zoom of the associated {@link Camera} if available.
	 */
	@Override
	public boolean handleInput(InputState pInputState) {
		if (mCamera == null)
			return false;

		// static zoom factor
		if (mAllowZoom) {
			mZoomAcceleration += pInputState.mouseWheelYOffset() * ZOOM_ACCELERATE_AMOUNT * pInputState.gameTime().elapseGameTimeSeconds() * mCamera.zoomFactor();
		}

		return super.handleInput(pInputState);

	}

	/**
	 * Controls the zoom factor of the associated {@link Camera} object, if present and applicable.
	 */
	public void update(final GameTime pGameTime) {
		if (this.mCamera == null)
			return;

		final float DELTA_TIME = (float) pGameTime.elapseGameTimeSeconds();
		float lZoomFactor = mCamera.zoomFactor();

		// apply zoom //
		mZoomVelocity += mZoomAcceleration;
		mZoomVelocity *= ZOOM_VELOCITY_DRAG;
		lZoomFactor += mZoomVelocity * DELTA_TIME;
		mZoomAcceleration = 0.0f;

		// Check bounds
		if (lZoomFactor < mCameraMinZoom)
			lZoomFactor = mCameraMinZoom;
		if (lZoomFactor > mCameraMaxZoom)
			lZoomFactor = mCameraMaxZoom;

		// Apply the new zoom factor to the camera object
		mCamera.setZoomFactor(lZoomFactor);
	}

}
