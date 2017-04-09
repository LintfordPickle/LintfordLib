package net.ld.library.controllers;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.input.InputState;
import net.ld.library.core.time.GameTime;

/** Controls the zoom factor of a {@link Camera} object. */
public class CameraZoomController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	/** Specifies the minimum amount of camera zoom */
	public static final float MIN_CAMERA_ZOOM = 0.75f;

	/** Specifies the maximum amount of camera zoom */
	public static final float MAX_CAMERA_ZOOM = 1.4f;

	/**
	 * Specifies the amount of DRAG to be applied to the zoom factor velocity
	 * over time.
	 */
	public static final float ZOOM_VELOCITY_DRAG = 0.987f;

	/**
	 * A coefficient for the speed of the zoom (modifys the mouse scroll wheel
	 * speed)
	 */
	public static final float ZOOM_ACCELERATE_AMOUNT = 9.0f;

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
	 * Sets the {@link Camera} object this controller works with. If null, the
	 * controller will skip its update calls.
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
			mCameraMaxZoom = pMax;

		}

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/** Ctor. */
	public CameraZoomController() {

		this.mCameraMinZoom = mCameraMaxZoom = 1.0f;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Listens to mouse scroll wheel input and updates the zoom of the
	 * associated {@link Camera} if available.
	 */
	public void handleInput(final InputState pInputState) {
		if (mCamera == null)
			return;

		// static zoom factor
		if (mAllowZoom) {
			mZoomAcceleration += pInputState.mouseWheelYOffset() * ZOOM_ACCELERATE_AMOUNT
					* pInputState.gameTime().elapseGameTime() / 1000f * mCamera.zoomFactor();
		}

	}

	/**
	 * Controls the zoom factor of the associated {@link Camera} object, if
	 * present and applicable.
	 */
	public void update(final GameTime pGameTime) {
		if (this.mCamera == null)
			return;

		final float DELTA_TIME = (float) pGameTime.elapseGameTime() / 1000.0f;
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
