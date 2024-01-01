package net.lintfordlib.controllers.camera;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;

public class CameraChaseController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Ship Chase Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private ICameraChaseTarget mTrackedTarget;
	private boolean mAllowManualControl;
	private boolean mIsTrackingPlayer;

	private Vector2f mVelocity;
	public Vector2f mDesiredPosition;
	public Vector2f mPosition;
	public Vector2f mLookAhead;

	public float mZoomFactor;
	public float mZoomVelocity;

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
	public boolean isInitialized() {
		return mGameCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraChaseController(ControllerManager controllerManager, ICamera camera, ICameraChaseTarget trackedTarget, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mVelocity = new Vector2f();
		mDesiredPosition = new Vector2f();
		mPosition = new Vector2f();
		mLookAhead = new Vector2f();

		mTrackedTarget = trackedTarget;
		mPosition.x = mTrackedTarget.worldX();
		mPosition.y = mTrackedTarget.worldX();

		mGameCamera = camera;
		mIsTrackingPlayer = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {

	}

	public void setCamera(ICamera cameCamera) {
		mGameCamera = cameCamera;
	}

	public void setTrackedEntity(ICameraChaseTarget trackedTarget) {
		mTrackedTarget = trackedTarget;
		mPosition.x = mTrackedTarget.worldX();
		mPosition.y = mTrackedTarget.worldX();
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
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		if (mTrackedTarget != null) {
			updateSpring(pCore);

			mGameCamera.setPosition(mTrackedTarget.worldX(), mTrackedTarget.worldY());
		}
	}

	private void updateSpring(LintfordCore core) {
		mStiffness = 10000.0f;
		mDamping = 1000.0f;
		mMass = 100.f;

		updatewWorldPositions(core);
		updateWorldZoomFactor(core);

		float elapsed = (float) core.gameTime().elapsedTimeMilli() * 0.001f;

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

	private void updatewWorldPositions(LintfordCore core) {
		if (mTrackedTarget == null)
			return;

		mLookAhead.x = mTrackedTarget.lookAheadX();
		mLookAhead.y = mTrackedTarget.lookAheadY();

		float lSpeedMod = mTrackedTarget.speed();
		mDesiredPosition.x = mTrackedTarget.worldX() + mLookAhead.x * lSpeedMod;
		mDesiredPosition.y = mTrackedTarget.worldY() + mLookAhead.y * lSpeedMod;
	}

	private void updateWorldZoomFactor(LintfordCore core) {
		final var lZoomOutLimit = 0.8f;
		final var lZoomInLimit = 1.5f;
		final var lDefaultZoom = lZoomInLimit;

		var lEntitySpeed = mTrackedTarget.speed();
		if (Math.abs(lEntitySpeed) < 0.001f)
			lEntitySpeed = 0.f;
		float lTargetZoom = lDefaultZoom - lEntitySpeed;
		lTargetZoom = MathHelper.clamp(lTargetZoom, lZoomOutLimit, lZoomInLimit);

		final float lVelStepSize = 0.175f;

		if (lTargetZoom > mZoomFactor)
			mZoomVelocity += lVelStepSize;
		else
			mZoomVelocity -= lVelStepSize;

		mZoomFactor += mZoomVelocity * core.gameTime().elapsedTimeMilli() * 0.001f;

		mZoomVelocity = MathHelper.clamp(mZoomVelocity, -0.025f, 0.025f);
		mZoomFactor = MathHelper.clamp(mZoomFactor, lZoomOutLimit, lZoomInLimit);
		mGameCamera.setZoomFactor(1);

		mZoomVelocity *= 0.0987f;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float zoomFactor) {
		mGameCamera.setZoomFactor(zoomFactor);
	}
}
