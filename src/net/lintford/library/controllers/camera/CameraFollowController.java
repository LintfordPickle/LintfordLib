package net.lintford.library.controllers.camera;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.entity.WorldEntity;
import net.lintford.library.core.maths.Vector2f;

public class CameraFollowController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "CameraFollowController";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 1f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private WorldEntity mTrackedEntity;
	private boolean mAllowManualControl;
	private boolean mIsTrackingPlayer;
	private Vector2f mVelocity;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mIsTrackingPlayer;
	}

	public void trackPlayer(boolean pNewValue) {
		mIsTrackingPlayer = pNewValue;
	}

	public boolean allowManualControl() {
		return mAllowManualControl;
	}

	public void allowManualControl(boolean pNewValue) {
		mAllowManualControl = pNewValue;
	}

	@Override
	public boolean isinitialized() {
		return mGameCamera != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraFollowController(ControllerManager pControllerManager, ICamera pCamera, WorldEntity pTrackEntity, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mVelocity = new Vector2f();

		//
		mGameCamera = pCamera;
		mTrackedEntity = pTrackEntity;
		mIsTrackingPlayer = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	// FIXME: Duplicate initialize method - rename to something else or clean up design!
	public void initialize(ICamera pGameCamera, WorldEntity pTrackedEntity) {
		mGameCamera = pGameCamera;
		mTrackedEntity = pTrackedEntity;

	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		if (mAllowManualControl) {
			final float speed = CAMERA_MAN_MOVE_SPEED;

			// Just listener for clicks - couldn't be easier !!?!
			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
				mVelocity.x -= speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
				mVelocity.x += speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
				mVelocity.y += speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
				mVelocity.y -= speed;
				mIsTrackingPlayer = false;

			}

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		mIsTrackingPlayer = mTrackedEntity != null;
		if (mIsTrackingPlayer) {
			mGameCamera.setPosition(-mTrackedEntity.worldPositionX, -mTrackedEntity.worldPositionY);

		} else {
			// Cap
			if (mVelocity.x < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.x > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.x = CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y < -CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = -CAMERA_MAN_MOVE_SPEED_MAX;
			if (mVelocity.y > CAMERA_MAN_MOVE_SPEED_MAX)
				mVelocity.y = CAMERA_MAN_MOVE_SPEED_MAX;

			float elapsed = (float) pCore.appTime().elapseTimeMilli();

			// Apply
			float lCurX = mGameCamera.getPosition().x;
			float lCurY = mGameCamera.getPosition().y;

			mGameCamera.setPosition(lCurX + mVelocity.x * elapsed, lCurY + mVelocity.y * elapsed);

		}

		// DRAG
		mVelocity.x *= 0.917f;
		mVelocity.y *= 0.917f;

		// There are minimums for the camera

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float pZoomFactor) {
		mGameCamera.setZoomFactor(pZoomFactor);

	}

}