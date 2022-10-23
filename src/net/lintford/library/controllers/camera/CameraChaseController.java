package net.lintford.library.controllers.camera;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.entity.WorldEntity;
import net.lintford.library.core.maths.Vector2f;

public class CameraChaseController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Chase Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private WorldEntity mTrackedEntity;
	private boolean mAllowManualControl;
	private boolean mIsTrackingPlayer;

	private Vector2f mVelocity;
	private Vector2f mDesiredPosition;
	private Vector2f mPosition;
	private Vector2f mLookAhead;

	private float mStiffness = 18.0f;
	private float mDamping = 6.0f;
	private float mMass = .5f;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mIsTrackingPlayer;
	}

	public void trackPlayer(boolean enableTrackPlayer) {
		mIsTrackingPlayer = enableTrackPlayer;
	}

	public boolean allowManualControl() {
		return mAllowManualControl;
	}

	public void allowManualControl(boolean allowManualControl) {
		mAllowManualControl = allowManualControl;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraChaseController(ControllerManager controllerManager, ICamera camera, WorldEntity trackEntity, int controllerGroup) {
		super(controllerManager, CONTROLLER_NAME, controllerGroup);

		mVelocity = new Vector2f();
		mDesiredPosition = new Vector2f();
		mPosition = new Vector2f();
		mLookAhead = new Vector2f();

		mPosition.x = trackEntity.x;
		mPosition.y = trackEntity.y;

		mGameCamera = camera;
		mTrackedEntity = trackEntity;
		mIsTrackingPlayer = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mGameCamera == null)
			return false;

		if (mAllowManualControl) {
			final float speed = CAMERA_MAN_MOVE_SPEED;

			// Just listener for clicks - couldn't be easier !!?!
			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
				mVelocity.x -= speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
				mVelocity.x += speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
				mVelocity.y += speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
				mVelocity.y -= speed;
				mIsTrackingPlayer = false;
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		if (mGameCamera == null)
			return;

		mStiffness = 30.0f;
		mDamping = 4.0f;
		mMass = .5f;

		if (mTrackedEntity != null) {
			updateSpring(core);

			mGameCamera.setPosition(-mPosition.x, -mPosition.y);
		}
	}

	private void updateSpring(LintfordCore core) {
		updatewWorldPositions(core);

		final float elapsed = (float) core.appTime().elapsedTimeSeconds();

		final float stretchX = mPosition.x - mDesiredPosition.x;
		final float stretchY = mPosition.y - mDesiredPosition.y;

		final float forceX = -mStiffness * stretchX - mDamping * mVelocity.x;
		final float forceY = -mStiffness * stretchY - mDamping * mVelocity.y;

		// Apply acceleration
		float accelerationX = forceX / mMass;
		float accelerationY = forceY / mMass;

		mVelocity.x += accelerationX * elapsed;
		mVelocity.y += accelerationY * elapsed;

		mPosition.x += mVelocity.x * elapsed;
		mPosition.y += mVelocity.y * elapsed;
	}

	private void updatewWorldPositions(LintfordCore core) {
		mLookAhead.x = mTrackedEntity.x + mVelocity.x;
		mLookAhead.y = mTrackedEntity.y + mVelocity.y;

		mDesiredPosition.x = mTrackedEntity.x;
		mDesiredPosition.y = mTrackedEntity.y;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float zoomFactor) {
		mGameCamera.setZoomFactor(zoomFactor);
	}
}