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

	public static final String CONTROLLER_NAME = "Camera Bounds Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;

	public Vector2f mCameraPositionRef;

	public float mZoomFactor;
	public float mZoomVelocity;

	public boolean mLimitBounds;

	private float mSceneWidthInPx;
	private float mSceneHeightInPx;
	private boolean mDrawBounds;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public void setBounds(float sceneWidthInPx, float sceneHeightInPx) {
		this.mSceneWidthInPx = sceneWidthInPx;
		this.mSceneHeightInPx = sceneHeightInPx;
	}

	public boolean drawBounds() {
		return mDrawBounds;
	}

	public void drawBounds(boolean shouldDrawBounds) {
		mDrawBounds = shouldDrawBounds;
	}

	public void widthBoundInPx(float width) {
		mSceneWidthInPx = width;
	}

	public float widthBoundInPx() {
		return mSceneWidthInPx;
	}

	public void heightBoundInPx(float height) {
		mSceneHeightInPx = height;
	}

	public float heightBoundInPx() {
		return mSceneHeightInPx;
	}

	public void limitBounds(boolean limitBounds) {
		mLimitBounds = limitBounds;
	}

	public boolean limitBounds() {
		return mLimitBounds;
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

		mSceneWidthInPx = 2048;
		mSceneHeightInPx = 2048;

		mLimitBounds = false;
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

		if (mLimitBounds) {

			final var lCamWidth = mGameCamera.getWidth();
			final var lCamHeight = mGameCamera.getHeight();

			// ensure camera doesn't go beyond scene extents
			if (mSceneWidthInPx != 0) {
				if (mCameraPositionRef.x - lCamWidth * .5f < -mSceneWidthInPx * 0.5f)
					mCameraPositionRef.x = -mSceneWidthInPx * 0.5f + lCamWidth * .5f;
				if (mCameraPositionRef.x + lCamWidth * .5f > mSceneWidthInPx * 0.5f)
					mCameraPositionRef.x = mSceneWidthInPx * 0.5f - lCamWidth * .5f;
			}

			if (mSceneHeightInPx != 0) {
				if (mCameraPositionRef.y - lCamHeight * .5f < -mSceneHeightInPx * 0.5f)
					mCameraPositionRef.y = -mSceneHeightInPx * 0.5f + lCamHeight * .5f;
				if (mCameraPositionRef.y + lCamHeight * .5f > mSceneHeightInPx * 0.5f)
					mCameraPositionRef.y = mSceneHeightInPx * 0.5f - lCamHeight * .5f;

			}
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float zoomFactor) {
		mGameCamera.setZoomFactor(zoomFactor);
	}

	public void resetCameraPosition() {
		mGameCamera.setZoomFactor(1.f);
		mGameCamera.setPosition(0.f, 0.f);

	}
}
