package net.lintfordlib.controllers.camera;

import java.util.Random;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.maths.Vector2f;

public class ScreenShakeController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Screen Shake Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected Camera mGameCamera;
	protected Random mRandom = new Random();
	protected float mShakeMag;
	protected float mShakeDur;
	protected float mShakeTimer;
	protected final Vector2f mOffsetPosition = new Vector2f();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public void setCamera(Camera camera) {
		mGameCamera = camera;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ScreenShakeController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mGameCamera == null)
			return;

		if (mShakeTimer > 0.f) {
			mShakeTimer -= core.appTime().elapsedTimeMilli();

			final var progress = mShakeTimer / mShakeDur;
			final var lMagnitude = mShakeMag * (1f - (progress * progress));

			mOffsetPosition.x = mRandom.nextFloat() * lMagnitude;
			mOffsetPosition.y = mRandom.nextFloat() * lMagnitude;

			mGameCamera.setCameraOffset(mOffsetPosition.x, mOffsetPosition.y);

		} else {
			mOffsetPosition.x = 0.f;
			mOffsetPosition.y = 0.f;

			mGameCamera.setCameraOffset(mOffsetPosition.x, mOffsetPosition.y);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void shakeCamera(float duration, float magnitude) {
		// don't interrupt large shakes with little ones
		if (mShakeTimer > 0.f) {
			if (mShakeMag > magnitude)
				return;
		}

		mShakeMag = magnitude;
		mShakeDur = Math.max(duration, mShakeDur);

		mShakeTimer = duration;
	}
}