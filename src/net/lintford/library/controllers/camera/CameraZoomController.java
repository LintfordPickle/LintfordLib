package net.lintford.library.controllers.camera;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.maths.MathHelper;

/** Controls the zoom factor of a {@link Camera} object. */
public class CameraZoomController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraZoomController";

	/**
	 * Specifies the amount of DRAG to be applied to the zoom factor velocity over time.
	 */
	public static final float ZOOM_VELOCITY_DRAG = 0.967f;

	/**
	 * A coefficient for the speed of the zoom (modifies the mouse scroll wheel speed)
	 */
	public static final float ZOOM_ACCELERATE_AMOUNT = 10.0f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	/** The associated {@link Camera} object this controller should control. */
	private ICamera mCamera;

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
	public void setCamera(ICamera pCamera) {
		this.mCamera = pCamera;
	}

	public void setZoomConstraints(float pMin, float pMax) {
		mCameraMinZoom = pMin;

		if (pMin > pMax)
			pMin = pMax;

		if (pMin > pMax) {
			// Just crazy talk
			pMin = pMax = mCameraMaxZoom = 1f;

		} else {
			mCameraMinZoom = pMin;
			mCameraMaxZoom = pMax;

		}

	}

	public float zoomFactor() {
		return mCamera.getZoomFactor();
	}

	public void zoomFactor(float pNewValue) {
		mCamera.setZoomFactor(pNewValue);
	}

	@Override
	public boolean isinitialized() {
		return mCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/** Ctor. */
	public CameraZoomController(ControllerManager pControllerManager, ICamera pCamera, int pControllerBaseGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerBaseGroup);

		mCamera = pCamera;
		this.mCameraMinZoom = 0.1f;
		mCameraMaxZoom = 3.0f;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		mCamera = null;

	}

	/**
	 * Listens to mouse scroll wheel input and updates the zoom of the associated {@link Camera} if available.
	 */
	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mCamera == null)
			return false;

		// static zoom factor
		if (mAllowZoom && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			mZoomAcceleration += pCore.input().mouse().mouseWheelYOffset() * mCamera.getZoomFactor();

		}

		return super.handleInput(pCore);

	}

	/**
	 * Controls the zoom factor of the associated {@link Camera} object, if present and applicable.
	 */
	@Override
	public void update(LintfordCore pCore) {
		if (this.mCamera == null)
			return;

		final float lDeltaTime = (float) pCore.appTime().elapsedTimeSeconds();
		float lZoomFactor = mCamera.getZoomFactor();

		// apply zoom //
		mZoomVelocity += mZoomAcceleration;
		lZoomFactor += mZoomVelocity * lDeltaTime;
		mZoomVelocity *= 0.85f;
		mZoomAcceleration = 0.0f;

		// Check bounds
		if (lZoomFactor < mCameraMinZoom) {
			lZoomFactor = mCameraMinZoom;
			mZoomVelocity = 0;
		}

		if (lZoomFactor > mCameraMaxZoom) {
			lZoomFactor = mCameraMaxZoom;
			mZoomVelocity = 0;
		}

		// Apply the new zoom factor to the camera object
		mCamera.setZoomFactor(MathHelper.round(lZoomFactor, 2));

	}

}
