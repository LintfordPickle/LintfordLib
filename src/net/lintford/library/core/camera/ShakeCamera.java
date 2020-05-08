package net.lintford.library.core.camera;

import java.util.Random;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.options.DisplayManager;

public class ShakeCamera extends Camera {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected Random mRandom = new Random();
	protected boolean mIsShaking;
	protected float mShakeMag;
	protected float mShakeDur;
	protected float mShakeTimer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ShakeCamera(DisplayManager pDisplayConfig) {
		super(pDisplayConfig);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore pCore) {
		if (mIsShaking) {

			mShakeTimer += pCore.appTime().elapsedTimeMilli();

			if (mShakeTimer > mShakeDur) {
				mIsShaking = false;
				mShakeTimer = mShakeDur;
			}

			// normal time
			float progress = mShakeTimer / mShakeDur;

			float lMagnitude = mShakeMag * (1f - (progress * progress));

			mOffsetPosition.x = mRandom.nextFloat() * lMagnitude;
			mOffsetPosition.y = mRandom.nextFloat() * lMagnitude;

		}

		super.update(pCore);
		
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * Shakes the camera for the specified amount of time (milliseconds) with the specified magnitude (pxs)
	 */
	public void shake(float pDuration, float pMagnitude) {
		if (pDuration <= 0)
			return;

//		mIsShaking = true;
//		mShakeDur = pDuration;
//		mShakeMag = pMagnitude;
//
//		mShakeTimer = 0;

	}

}
