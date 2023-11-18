package net.lintfordlib.controllers.camera;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.maths.Vector2f;

public class CameraBoundsController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Editor Camera Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;

	public Vector2f mCameraPositionRef;

	public float mZoomFactor;
	public float mZoomVelocity;

	// TODO: separate bounds
	public float sceneWidthInPx;
	public float sceneHeightInPx;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public void setBounds(float sceneWidthInPx, float sceneHeightInPx) {
		this.sceneWidthInPx = sceneWidthInPx;
		this.sceneHeightInPx = sceneHeightInPx;
	}

	@Override
	public boolean isInitialized() {
		return mGameCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraBoundsController(ControllerManager controllerManager, ICamera camera, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mGameCamera = camera;
		mCameraPositionRef = mGameCamera.getPosition();

		// TODO: DEBUG
		sceneWidthInPx = 2048;
		sceneHeightInPx = 2048;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unloadController() {

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		final var lCamWidth = mGameCamera.getWidth();
		final var lCamHeight = mGameCamera.getHeight();

		// TODO: Lots more work here (need to consider the zoom)

		// ensure camera doesn't go beyond scene extents
		if (sceneWidthInPx != 0) {
			if (mCameraPositionRef.x - lCamWidth * .5f < -sceneWidthInPx * 0.5f)
				mGameCamera.setPosition(-sceneWidthInPx * 0.5f + lCamWidth * .5f, mCameraPositionRef.y);
			if (mCameraPositionRef.x + lCamWidth * .5f > sceneWidthInPx * 0.5f)
				mCameraPositionRef.x = sceneWidthInPx * 0.5f - lCamWidth * .5f;
		}

		if (sceneHeightInPx != 0) {
			if (mCameraPositionRef.y - lCamHeight * .5f < -sceneHeightInPx * 0.5f)
				mCameraPositionRef.y = -sceneHeightInPx * 0.5f + lCamHeight * .5f;
			if (mCameraPositionRef.y + lCamHeight * .5f > sceneHeightInPx * 0.5f)
				mCameraPositionRef.y = sceneHeightInPx * 0.5f - lCamHeight * .5f;
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float zoomFactor) {
		mGameCamera.setZoomFactor(zoomFactor);
	}
}
