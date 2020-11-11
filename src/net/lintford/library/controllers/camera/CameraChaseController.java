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

	public CameraChaseController(ControllerManager pControllerManager, ICamera pCamera, WorldEntity pTrackEntity, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mVelocity = new Vector2f();
		mDesiredPosition = new Vector2f();
		mPosition = new Vector2f();
		mLookAhead = new Vector2f();

		mPosition.x = pTrackEntity.worldPositionX;
		mPosition.y = pTrackEntity.worldPositionY;

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

		if (mTrackedEntity != null) {
			updateSpring(pCore);

			mGameCamera.setPosition(-mPosition.x, -mPosition.y);

		}

	}

	private void updateSpring(LintfordCore pCore) {
		updatewWorldPositions(pCore);

		float elapsed = (float) pCore.appTime().elapsedTimeSeconds();

		// Calculate spring force
		float stretchX = mPosition.x - mDesiredPosition.x;
		float stretchY = mPosition.y - mDesiredPosition.y;

		float forceX = -mStiffness * stretchX - mDamping * mVelocity.x;
		float forceY = -mStiffness * stretchY - mDamping * mVelocity.y;

		// Apply acceleration
		float accelerationX = forceX / mMass;
		float accelerationY = forceY / mMass;

		mVelocity.x += accelerationX * elapsed;
		mVelocity.y += accelerationY * elapsed;

		// Apply velocity
		mPosition.x += mVelocity.x * elapsed;
		mPosition.y += mVelocity.y * elapsed;

	}

	private void updatewWorldPositions(LintfordCore pCore) {
		mLookAhead.x = mTrackedEntity.worldPositionX + mVelocity.x;
		mLookAhead.y = mTrackedEntity.worldPositionY + mVelocity.y;

		mDesiredPosition.x = mTrackedEntity.worldPositionX;
		mDesiredPosition.y = mTrackedEntity.worldPositionY;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float pZoomFactor) {
		mGameCamera.setZoomFactor(pZoomFactor);

	}

}
