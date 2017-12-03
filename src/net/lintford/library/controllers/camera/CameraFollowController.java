package net.lintford.library.controllers.camera;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.entities.WorldEntity;

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
	private boolean mTrackPlayer;
	private Vector2f mVelocity;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mTrackPlayer;
	}

	public void trackPlayer(boolean pNewValue) {
		mTrackPlayer = pNewValue;
	}

	@Override
	public boolean isInitialised() {
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
		mTrackPlayer = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialise(ICamera pGameCamera, WorldEntity pTrackedEntity) {
		mGameCamera = pGameCamera;
		mTrackedEntity = pTrackedEntity;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		final float speed = CAMERA_MAN_MOVE_SPEED;

		// Just listener for clicks - couldn't be easier !!?!
		if (pCore.input().keyDown(GLFW.GLFW_KEY_A)) {
			mVelocity.x -= speed;
			mTrackPlayer = false;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_D)) {
			mVelocity.x += speed;
			mTrackPlayer = false;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_S)) {
			mVelocity.y += speed;
			mTrackPlayer = false;

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_W)) {
			mVelocity.y -= speed;
			mTrackPlayer = false;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			;
		mTrackPlayer = true;
		if (mTrackPlayer && mTrackedEntity != null) {

			mGameCamera.setPosition(-mTrackedEntity.x, -mTrackedEntity.y);

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

			float elapsed = (float) pCore.time().elapseGameTimeMilli();

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

	public void zoomIn() {
		mGameCamera.setZoomFactor(CameraZoomController.MAX_CAMERA_ZOOM);

	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------
}
