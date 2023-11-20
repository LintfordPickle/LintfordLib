package net.lintfordlib.controllers.camera;

import net.lintfordlib.core.maths.Vector2f;
import org.lwjgl.glfw.GLFW;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.geometry.Rectangle;

public class CameraMovementController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Movement Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 15.f;
	private static final float CAMERA_MAN_MOVE_SPEED_MAX = 10f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Rectangle mPlayArea;
	private ICamera mGameCamera;
	private Vector2f mVelocity;
	private Vector2f mPointToFollow;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isFollowingPoint() {
		return mPointToFollow != null;
	}

	public void setPointToFollow(Vector2f pointToFollow) {
		mPointToFollow = pointToFollow;
	}

	public void clearPlayArea() {
		mPlayArea.set(0, 0, 0, 0);
	}

	public Rectangle playArea() {
		return mPlayArea;
	}

	public void setPlayArea(float pX, float pY, float pWidth, float pHeight) {
		mPlayArea.set(pX, pY, pWidth, pHeight);
	}

	public ICamera gameCamera() {
		return mGameCamera;
	}

	@Override
	public boolean isInitialized() {
		return mGameCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraMovementController(ControllerManager controllerManager, ICamera camera, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mVelocity = new Vector2f();
		mPlayArea = new Rectangle();
		if (camera instanceof Camera) {
			final var lCamera = (Camera) camera;
			lCamera.setIsChaseCamera(true, 0.06f);
		}

		mGameCamera = camera;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mGameCamera == null)
			return false;

		final float lElapsed = (float) core.appTime().elapsedTimeMilli() * 0.001f;
		final float lOneOverCameraZoom = mGameCamera.getZoomFactorOverOne();
		final float speed = CAMERA_MAN_MOVE_SPEED * lOneOverCameraZoom;

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			return false; // editor controls

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
			mVelocity.x -= speed * lElapsed;
			mPointToFollow = null; // stop auto follow

		}
		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
			mVelocity.x += speed * lElapsed;
			mPointToFollow = null; // stop auto follow

		}
		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
			mVelocity.y += speed * lElapsed;
			mPointToFollow = null; // stop auto follow

		}
		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
			mVelocity.y -= speed * lElapsed;
			mPointToFollow = null; // stop auto follow

		}
		return false;
	}

	@Override
	public void update(LintfordCore core) {
		if (mGameCamera == null)
			return;

		if (mPointToFollow != null) {
			mGameCamera.setPosition(mPointToFollow.x, mPointToFollow.y);

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

			float elapsed = (float) core.appTime().elapsedTimeMilli();

			// Applys
			float lCurX = mGameCamera.getPosition().x;
			float lCurY = mGameCamera.getPosition().y;
			if (mPlayArea != null && !mPlayArea.isEmpty()) {
				if (lCurX - mGameCamera.getWidth() * .5f < mPlayArea.left()) {
					lCurX = mPlayArea.left() + mGameCamera.getWidth() * .5f;
					if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) // kill velocity
						mVelocity.x = 0;
				}
				if (lCurX + mGameCamera.getWidth() * .5f > mPlayArea.right()) {
					lCurX = mPlayArea.right() - mGameCamera.getWidth() * .5f;
					if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) // kill velocity
						mVelocity.x = 0;
				}
				if (lCurY - mGameCamera.getHeight() * .5f < mPlayArea.top()) {
					lCurY = mPlayArea.top() + mGameCamera.getHeight() * .5f;
					if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) // kill velocity
						mVelocity.y = 0;
				}
				if (lCurY + mGameCamera.getHeight() * .5f > mPlayArea.bottom()) {
					lCurY = mPlayArea.bottom() - mGameCamera.getHeight() * .5f;
					if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) // kill velocity
						mVelocity.y = 0;
				}
			}

			mGameCamera.setPosition(lCurX + mVelocity.x * elapsed, lCurY + mVelocity.y * elapsed);
		}

		// DRAG
		mVelocity.x *= 0.857f;
		mVelocity.y *= 0.857f;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float pZoomFactor) {
		mGameCamera.setZoomFactor(pZoomFactor);
	}

}
